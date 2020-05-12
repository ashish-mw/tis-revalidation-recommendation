package uk.nhs.hee.tis.revalidation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.hee.tis.revalidation.dto.*;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.by;
import static uk.nhs.hee.tis.revalidation.entity.UnderNotice.ON_HOLD;
import static uk.nhs.hee.tis.revalidation.entity.UnderNotice.YES;

@Transactional
@Service
public class DoctorsForDBService {


    @Value("${app.reval.pagination.pageSize}")
    private int pageSize;

    @Autowired
    private DoctorsForDBRepository doctorsRepository;

    @Autowired
    private TraineeCoreService traineeCoreService;

    public TraineeDoctorDTO getAllTraineeDoctorDetails(final RevalidationRequestDTO requestDTO) {
        final var paginatedDoctors = getSortedAndFilteredDoctorsByPageNumber(requestDTO);
        final var doctorsList = paginatedDoctors.get().collect(toList());
        final var gmcIds = doctorsList.stream().map(doc -> doc.getGmcReferenceNumber()).collect(toList());
        final var traineeCoreInfo = traineeCoreService.getTraineeInformationFromCore(gmcIds);
        final var traineeDoctors = doctorsList.stream().map(d ->
                convert(d, traineeCoreInfo.get(d.getGmcReferenceNumber()))).collect(toList());

        return TraineeDoctorDTO.builder()
                .traineeInfo(traineeDoctors)
                .countTotal(getCountAll())
                .countUnderNotice(getCountUnderNotice())
                .totalPages(paginatedDoctors.getTotalPages())
                .totalResults(paginatedDoctors.getTotalElements())
                .build();
    }

    private TraineeInfoDTO convert(final DoctorsForDB doctorsForDB, final TraineeCoreDTO traineeCoreDTO) {
        final var traineeInfoDTOBuilder = TraineeInfoDTO.builder()
                .gmcReferenceNumber(doctorsForDB.getGmcReferenceNumber())
                .doctorFirstName(doctorsForDB.getDoctorFirstName())
                .doctorLastName(doctorsForDB.getDoctorLastName())
                .submissionDate(doctorsForDB.getSubmissionDate())
                .dateAdded(doctorsForDB.getDateAdded())
                .underNotice(doctorsForDB.getUnderNotice())
                .sanction(doctorsForDB.getSanction())
                .doctorStatus(doctorsForDB.getDoctorStatus());

        if (traineeCoreDTO != null) {
            traineeInfoDTOBuilder
                    .cctDate(traineeCoreDTO.getCctDate())
                    .programmeName(traineeCoreDTO.getProgrammeName())
                    .programmeMembershipType(traineeCoreDTO.getProgrammeMembershipType())
                    .currentGrade(traineeCoreDTO.getCurrentGrade());
        }

        return traineeInfoDTOBuilder.build();

    }

    private Page<DoctorsForDB> getSortedAndFilteredDoctorsByPageNumber(final RevalidationRequestDTO requestDTO) {
        final var direction = "asc".equalsIgnoreCase(requestDTO.getSortOrder()) ? ASC : DESC;
        final var pageableAndSortable = of(requestDTO.getPageNumber(), pageSize, by(direction, requestDTO.getSortColumn()));
        if (requestDTO.isUnderNotice()) {
            return doctorsRepository.findAllByUnderNoticeIn(pageableAndSortable, requestDTO.getSearchQuery(), YES, ON_HOLD);
        }

        return doctorsRepository.findAll(pageableAndSortable, requestDTO.getSearchQuery());
    }

    //TODO: explore to implement cache
    private long getCountAll() {
        return doctorsRepository.count();
    }

    //TODO: explore to implement cache
    private long getCountUnderNotice() {
        return doctorsRepository.countByUnderNoticeIn(YES, ON_HOLD);
    }

    public void updateTrainee(final DoctorsForDBDTO gmcDoctor) {
        final DoctorsForDB doctorsForDB = DoctorsForDB.convert(gmcDoctor);
        doctorsRepository.save(doctorsForDB);
    }
}

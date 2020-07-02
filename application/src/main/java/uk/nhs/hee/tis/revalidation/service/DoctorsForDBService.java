package uk.nhs.hee.tis.revalidation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.hee.tis.revalidation.dto.*;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.exception.RecommendationException;
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

    public TraineeSummaryDto getAllTraineeDoctorDetails(final TraineeRequestDto requestDTO) {
        final var paginatedDoctors = getSortedAndFilteredDoctorsByPageNumber(requestDTO);
        final var doctorsList = paginatedDoctors.get().collect(toList());
        final var gmcIds = doctorsList.stream().map(doc -> doc.getGmcReferenceNumber()).collect(toList());
        final var traineeCoreInfo = traineeCoreService.getTraineeInformationFromCore(gmcIds);
        final var traineeDoctors = doctorsList.stream().map(d ->
                convert(d, traineeCoreInfo.get(d.getGmcReferenceNumber()))).collect(toList());

        return TraineeSummaryDto.builder()
                .traineeInfo(traineeDoctors)
                .countTotal(getCountAll())
                .countUnderNotice(getCountUnderNotice())
                .totalPages(paginatedDoctors.getTotalPages())
                .totalResults(paginatedDoctors.getTotalElements())
                .build();
    }

    public void updateTrainee(final DoctorsForDbDto gmcDoctor) {
        final var doctorsForDB = DoctorsForDB.convert(gmcDoctor);
        final var doctor = doctorsRepository.findById(gmcDoctor.getGmcReferenceNumber());
        if (doctor.isPresent()) {
            doctorsForDB.setAdmin(doctor.get().getAdmin());
        }
        doctorsRepository.save(doctorsForDB);
    }

    public void updateTraineeAdmin(final String gmcNumber, final String admin) {
        final var doctor = doctorsRepository.findById(gmcNumber);
        if (doctor.isPresent()) {
            final var doctorsForDB = doctor.get();
            doctorsForDB.setAdmin(admin);
            doctorsRepository.save(doctorsForDB);
        } else {
            throw new RecommendationException("No trainee found to update");
        }
    }

    private TraineeInfoDto convert(final DoctorsForDB doctorsForDB, final TraineeCoreDto traineeCoreDTO) {
        final var traineeInfoDTOBuilder = TraineeInfoDto.builder()
                .gmcReferenceNumber(doctorsForDB.getGmcReferenceNumber())
                .doctorFirstName(doctorsForDB.getDoctorFirstName())
                .doctorLastName(doctorsForDB.getDoctorLastName())
                .submissionDate(doctorsForDB.getSubmissionDate())
                .dateAdded(doctorsForDB.getDateAdded())
                .underNotice(doctorsForDB.getUnderNotice().name())
                .sanction(doctorsForDB.getSanction())
                .doctorStatus(doctorsForDB.getDoctorStatus().name()) //TODO update with legacy statuses
                .lastUpdatedDate(doctorsForDB.getLastUpdatedDate())
                .admin(doctorsForDB.getAdmin());

        if (traineeCoreDTO != null) {
            traineeInfoDTOBuilder
                    .cctDate(traineeCoreDTO.getCctDate())
                    .programmeName(traineeCoreDTO.getProgrammeName())
                    .programmeMembershipType(traineeCoreDTO.getProgrammeMembershipType())
                    .currentGrade(traineeCoreDTO.getCurrentGrade());
        }

        return traineeInfoDTOBuilder.build();

    }

    private Page<DoctorsForDB> getSortedAndFilteredDoctorsByPageNumber(final TraineeRequestDto requestDTO) {
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

}

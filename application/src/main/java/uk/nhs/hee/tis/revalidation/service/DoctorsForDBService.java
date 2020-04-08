package uk.nhs.hee.tis.revalidation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.hee.tis.revalidation.dto.RevalidationRequestDTO;
import uk.nhs.hee.tis.revalidation.dto.TraineeDoctorDTO;
import uk.nhs.hee.tis.revalidation.dto.TraineeInfoDTO;
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

    public TraineeDoctorDTO getAllTraineeDoctorDetails(final RevalidationRequestDTO requestDTO) {
        final var paginatedDoctors = getSortedAndFilteredDoctorsByPageNumber(requestDTO);
        final var traineeDoctors = paginatedDoctors.get().map(d -> convert(d)).collect(toList());

        return TraineeDoctorDTO.builder()
                .traineeInfo(traineeDoctors)
                .countTotal(getCountAll())
                .countUnderNotice(getCountUnderNotice())
                .totalPages(paginatedDoctors.getTotalPages())
                .build();
    }

    private TraineeInfoDTO convert(final DoctorsForDB doctorsForDB) {
        return TraineeInfoDTO.builder()
                .gmcReferenceNumber(doctorsForDB.getGmcReferenceNumber())
                .doctorFirstName(doctorsForDB.getDoctorFirstName())
                .doctorLastName(doctorsForDB.getDoctorLastName())
                .submissionDate(doctorsForDB.getSubmissionDate())
                .dateAdded(doctorsForDB.getDateAdded())
                .underNotice(doctorsForDB.getUnderNotice())
                .sanction(doctorsForDB.getSanction())
                .doctorStatus(doctorsForDB.getDoctorStatus())
                .build();
    }


    private Page<DoctorsForDB> getSortedAndFilteredDoctorsByPageNumber(final RevalidationRequestDTO requestDTO) {
        final var direction = "asc".equalsIgnoreCase(requestDTO.getSortOrder()) ? ASC : DESC;
        final Pageable pageableAndSortable = of(requestDTO.getPageNumber(), pageSize, by(direction, requestDTO.getSortColumn()));
        if (requestDTO.isUnderNotice()) {
            return doctorsRepository.findAllByUnderNoticeIn(pageableAndSortable, YES, ON_HOLD);
        }

        return doctorsRepository.findAll(pageableAndSortable);
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

package uk.nhs.hee.tis.revalidation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.hee.tis.revalidation.dto.RevalidationRequestDTO;
import uk.nhs.hee.tis.revalidation.dto.TraineeDoctorDTO;
import uk.nhs.hee.tis.revalidation.dto.TraineeInfoDTO;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.by;
import static uk.nhs.hee.tis.revalidation.entity.UnderNotice.NO;

@Transactional
@Service
public class DoctorsForDBService {

    @Autowired
    private DoctorsForDBRepository doctorsRepository;

    public TraineeDoctorDTO getAllTraineeDoctorDetails(final RevalidationRequestDTO requestDTO) {
        final var doctorsForDB = getAndConvertDoctorsForDB(requestDTO);
        return TraineeDoctorDTO.builder()
                .traineeInfo(doctorsForDB)
                .countTotal(doctorsForDB.size())
                .countUnderNotice(underNoticeCount())
                .build();
    }

    private List<TraineeInfoDTO> getAndConvertDoctorsForDB(final RevalidationRequestDTO requestDTO) {
        final var direction = "asc".equalsIgnoreCase(requestDTO.getSortOrder()) ? ASC : DESC;
        final var allDoctors = doctorsRepository.findAll(by(direction, requestDTO.getSortColumn()));
        return allDoctors.stream().map(d -> TraineeInfoDTO.builder()
                .gmcReferenceNumber(d.getGmcReferenceNumber())
                .doctorFirstName(d.getDoctorFirstName())
                .doctorLastName(d.getDoctorLastName())
                .submissionDate(d.getSubmissionDate())
                .dateAdded(d.getDateAdded())
                .underNotice(d.getUnderNotice())
                .sanction(d.getSanction())
                .doctorStatus(d.getDoctorStatus())
                .build())
                .collect(toList());
    }

    private long underNoticeCount() {
        return doctorsRepository.countByUnderNoticeIsNot(NO.name());
    }
}

package uk.nhs.hee.tis.revalidation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.hee.tis.revalidation.dto.DoctorsForDBDTO;
import uk.nhs.hee.tis.revalidation.dto.GmcDoctorDTO;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.by;

@Transactional
@Service
public class DoctorsForDBService {

    private static final String SUBMISSION_DATE = "submissionDate";

    @Autowired
    private DoctorsForDBRepository doctorsForDBRepository;

    public GmcDoctorDTO getAllTraineeDoctorDetails() {
        final var doctorsForDB = getAndConvertDoctorsForDB();
        return GmcDoctorDTO.builder()
                .doctorsForDB(doctorsForDB)
                .count(doctorsForDB.size())
                .build();
    }

    private List<DoctorsForDBDTO> getAndConvertDoctorsForDB() {
        final var allDoctors = doctorsForDBRepository.findAll(by(DESC, SUBMISSION_DATE));
        return allDoctors.stream().map(d -> DoctorsForDBDTO.builder()
                .gmcReferenceNumber(d.getGmcReferenceNumber())
                .doctorFirstName(d.getDoctorFirstName())
                .doctorLastName(d.getDoctorLastName())
                .submissionDate(d.getSubmissionDate())
                .dateAdded(d.getDateAdded())
                .underNotice(d.getUnderNotice().value())
                .sanction(d.getSanction())
                .build())
                .collect(Collectors.toList());
    }
}

package uk.nhs.hee.tis.revalidation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.hee.tis.revalidation.dto.DoctorsForDBDTO;
import uk.nhs.hee.tis.revalidation.dto.GmcDoctorDTO;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class DoctorsForDBService {

    @Autowired
    private DoctorsForDBRepository doctorsForDBRepository;

    public GmcDoctorDTO getAllTraineeDoctorDetails() {
        final var doctorsForDB = getAndConvertDoctorsForDB();
        return GmcDoctorDTO.builder().doctorsForDB(doctorsForDB).build();
    }

    private List<DoctorsForDBDTO> getAndConvertDoctorsForDB() {
        final var allDoctors = doctorsForDBRepository.findAll();
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

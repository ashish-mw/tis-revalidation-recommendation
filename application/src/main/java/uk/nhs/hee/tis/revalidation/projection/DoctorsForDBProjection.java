package uk.nhs.hee.tis.revalidation.projection;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.event.CreateDoctorsForDBEvent;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;

@Component
public class DoctorsForDBProjection {

    @Autowired
    private DoctorsForDBRepository repository;

    @EventHandler
    public void on(final CreateDoctorsForDBEvent event) {
        final var doctorsForDB = DoctorsForDB.builder()
                .gmcReferenceNumber(event.getGmcReferenceNumber())
                .doctorFirstName(event.getDoctorFirstName())
                .doctorLastName(event.getDoctorLastName())
                .submissionDate(event.getSubmissionDate())
                .dateAdded(event.getDateAdded())
                .underNotice(event.getUnderNotice())
                .sanction(event.getSanction())
                .doctorStatus(event.getDoctorStatus())
                .build();

        repository.save(doctorsForDB);
    }
}

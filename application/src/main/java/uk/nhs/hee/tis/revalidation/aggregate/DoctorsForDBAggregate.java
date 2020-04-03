package uk.nhs.hee.tis.revalidation.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.Repository;
import org.axonframework.spring.stereotype.Aggregate;
import uk.nhs.hee.tis.revalidation.command.CreateDoctorForDBCommand;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.event.CreateDoctorsForDBEvent;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Aggregate
public class DoctorsForDBAggregate implements Serializable {

    @AggregateIdentifier
    private UUID id;
    private String gmcReferenceNumber;
    private String doctorFirstName;
    private String doctorLastName;
    private LocalDate submissionDate;
    private LocalDate dateAdded;
    private UnderNotice underNotice;
    private String sanction;
    private String doctorStatus;

    @CommandHandler
    public DoctorsForDBAggregate(final CreateDoctorForDBCommand command) {
        log.info("Command Handler: {}", CreateDoctorForDBCommand.class.getName());
        final var event = CreateDoctorsForDBEvent.builder()
                .id(command.getId())
                .gmcReferenceNumber(command.getGmcReferenceNumber())
                .doctorFirstName(command.getDoctorFirstName())
                .doctorLastName(command.getDoctorLastName())
                .submissionDate(command.getSubmissionDate())
                .dateAdded(command.getDateAdded())
                .underNotice(UnderNotice.fromString(command.getUnderNotice()))
                .sanction(command.getSanction())
                .doctorStatus(command.getDoctorStatus())
                .build();



        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(final CreateDoctorsForDBEvent event) {
        log.info("Event Source Handler: {}", CreateDoctorsForDBEvent.class.getName());
        this.id = event.getId();
        this.gmcReferenceNumber = event.getGmcReferenceNumber();
        this.doctorFirstName = event.getDoctorFirstName();
        this.doctorLastName = event.getDoctorLastName();
        this.submissionDate = event.getSubmissionDate();
        this.dateAdded = event.getDateAdded();
        this.underNotice = event.getUnderNotice();
        this.sanction = event.getSanction();
        this.doctorStatus = event.getDoctorStatus();
    }

}

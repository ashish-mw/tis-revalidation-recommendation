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
import org.axonframework.spring.stereotype.Aggregate;
import uk.nhs.hee.tis.revalidation.command.DoctorForDBReceivedCommand;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.event.DoctorsForDBReceivedEvent;

import java.io.Serializable;
import java.time.LocalDate;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Aggregate
public class DoctorsForDBAggregate implements Serializable {

    @AggregateIdentifier
    private String gmcReferenceNumber;
    private String doctorFirstName;
    private String doctorLastName;
    private LocalDate submissionDate;
    private LocalDate dateAdded;
    private UnderNotice underNotice;
    private String sanction;

    @CommandHandler
    public DoctorsForDBAggregate(final DoctorForDBReceivedCommand command) {
        log.info("Command Handler *****");
        final var event = DoctorsForDBReceivedEvent.builder()
                .gmcReferenceNumber(command.getGmcReferenceNumber())
                .doctorFirstName(command.getDoctorFirstName())
                .doctorLastName(command.getDoctorLastName())
                .submissionDate(command.getSubmissionDate())
                .dateAdded(command.getDateAdded())
                .underNotice(UnderNotice.fromString(command.getUnderNotice()))
                .sanction(command.getSanction())
                .build();

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(final DoctorsForDBReceivedEvent event) {
        log.info("Event Source Handler *****");
        this.gmcReferenceNumber = event.getGmcReferenceNumber();
        this.doctorFirstName = event.getDoctorFirstName();
        this.doctorLastName = event.getDoctorLastName();
        this.submissionDate = event.getSubmissionDate();
        this.dateAdded = event.getDateAdded();
        this.underNotice = event.getUnderNotice();
        this.sanction = event.getSanction();
    }

}

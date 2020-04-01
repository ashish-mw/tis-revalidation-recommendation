package uk.nhs.hee.tis.revalidation.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class CreateDoctorForDBCommand {

    @TargetAggregateIdentifier
    private UUID id;
    private String gmcReferenceNumber;
    private String doctorFirstName;
    private String doctorLastName;
    private LocalDate submissionDate;
    private LocalDate dateAdded;
    private String underNotice;
    private String sanction;
}

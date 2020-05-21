package uk.nhs.hee.tis.revalidation.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "revalidation")
@ApiModel(description = "Trainee doctors's revalidation data")
public class Revalidation {

    private UUID id;
    private RevalidationGmcOutcome outcome;
    private RevalidationType revalidationType;
    private LocalDateTime gmcSubmissionDate;
    private LocalDateTime actualSubmissionDate;
    private String gmcRevalidationId;
    private String deferralDate;
    private DeferralReason deferralReason;
    private String deferralComment;
    private String admin;
}

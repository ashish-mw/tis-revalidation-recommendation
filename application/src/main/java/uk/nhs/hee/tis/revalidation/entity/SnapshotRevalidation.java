package uk.nhs.hee.tis.revalidation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SnapshotRevalidation {
    private String id;
    private String proposedOutcomeCode;
    private String deferralDate;
    private String deferralReason;
    private String deferralComment;
    private String revalidationStatusCode;
    private String gmcSubmissionDateTime;
    private String gmcSubmissionReturnCode;
    private String gmcRecommendationId;
    private String gmcOutcomeCode;
    private String gmcStatusCheckDateTime;
    private String admin;
    private String submissionDate;
    private String recommendationSubmitter;
    private String dateAdded;
}

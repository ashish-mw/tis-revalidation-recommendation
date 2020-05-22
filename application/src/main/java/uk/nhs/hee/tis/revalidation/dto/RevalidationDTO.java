package uk.nhs.hee.tis.revalidation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevalidationDTO {

    private String gmcOutcome;
    private String revalidationType;
    private Date gmcSubmissionDate;
    private Date actualSubmissionDate;
    private String gmcRevalidationId;
    private String revalidationStatus;
    private String deferralDate;
    private String deferralReason;
    private String deferralComment;
    private String admin;
}

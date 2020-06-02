package uk.nhs.hee.tis.revalidation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown =  true)
public class TraineeRecommendationRecordDTO {

    private String gmcNumber;
    private String recommendationId;
    private String gmcOutcome;
    private String recommendationType;
    private LocalDateTime gmcSubmissionDate;
    private LocalDate actualSubmissionDate;
    private String gmcRevalidationId;
    private String recommendationStatus;
    private LocalDate deferralDate;
    private String deferralReason;
    private String deferralSubReason;
    private String deferralComment;
    private List<String> comments;
    private String admin;
}

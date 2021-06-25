package uk.nhs.hee.tis.revalidation.entity;

import io.swagger.annotations.ApiModel;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "recommendation")
@ApiModel(description = "Trainee doctors's recommendation data")
public class Recommendation {

  @Id
  private String id;
  private String gmcNumber;
  private RecommendationGmcOutcome outcome;
  private RecommendationType recommendationType;
  private RecommendationStatus recommendationStatus;
  private LocalDate gmcSubmissionDate;
  private LocalDate actualSubmissionDate;
  private String gmcRevalidationId;
  private LocalDate deferralDate;
  private String deferralReason;
  private String deferralSubReason;
  private List<String> comments;
  private String admin;
}

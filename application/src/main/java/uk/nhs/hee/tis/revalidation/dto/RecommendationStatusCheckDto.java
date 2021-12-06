package uk.nhs.hee.tis.revalidation.dto;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;
import uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome;

@Data
@Builder
@ApiModel(description = "DTO for recommendation status check from GMC")
public class RecommendationStatusCheckDto {

  private String designatedBodyId;
  private String gmcReferenceNumber;
  private String gmcRecommendationId;
  private String recommendationId;
  private RecommendationGmcOutcome outcome;
}

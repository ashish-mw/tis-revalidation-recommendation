package uk.nhs.hee.tis.revalidation.dto;

import io.swagger.annotations.ApiModel;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "List of trainee doctors with total count")
public class TraineeSummaryDto {

  private long countTotal;
  private long countUnderNotice;
  private long totalPages;
  private long totalResults;
  private List<TraineeInfoDto> traineeInfo;
}

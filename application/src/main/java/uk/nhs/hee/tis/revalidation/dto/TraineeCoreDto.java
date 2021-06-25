package uk.nhs.hee.tis.revalidation.dto;

import io.swagger.annotations.ApiModel;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Trainee information from core")
public class TraineeCoreDto {

  private String gmcId;
  private LocalDate cctDate;
  private String programmeMembershipType;
  private String programmeName;
  private String currentGrade;
}

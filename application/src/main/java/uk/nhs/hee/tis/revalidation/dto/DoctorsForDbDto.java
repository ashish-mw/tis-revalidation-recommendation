package uk.nhs.hee.tis.revalidation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Doctors information which represents DoctorForDB gmc data")
public class DoctorsForDbDto {

  private String gmcReferenceNumber;
  private String doctorFirstName;
  private String doctorLastName;
  private String submissionDate;
  private String dateAdded;
  private String underNotice;
  private String sanction;
  private String designatedBodyCode;
}

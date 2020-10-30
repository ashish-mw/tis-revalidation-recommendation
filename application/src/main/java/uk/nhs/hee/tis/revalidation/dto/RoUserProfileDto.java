package uk.nhs.hee.tis.revalidation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoUserProfileDto {

  private String userName;
  private String fullName;
  private String firstName;
  private String lastName;
  private String gmcId;
  private Set<String> designatedBodyCodes;
  private String phoneNumber;
  private String emailAddress;

}

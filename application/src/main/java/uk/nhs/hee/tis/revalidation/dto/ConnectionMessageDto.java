package uk.nhs.hee.tis.revalidation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ConnectionMessageDto {

  private String gmcId;
  private String designatedBodyCode;
}


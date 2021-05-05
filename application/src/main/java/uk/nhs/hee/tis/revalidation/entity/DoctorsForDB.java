package uk.nhs.hee.tis.revalidation.entity;


import static java.time.LocalDate.now;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationStatus.NOT_STARTED;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.convertGmcDateToLocalDate;

import io.swagger.annotations.ApiModel;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;
import uk.nhs.hee.tis.revalidation.dto.DoctorsForDbDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "doctorsForDB")
@ApiModel(description = "Trainee doctors's core profile data")
public class DoctorsForDB {

  @Id
  private String gmcReferenceNumber;
  private String doctorFirstName;
  private String doctorLastName;
  private LocalDate submissionDate;
  private LocalDate dateAdded;
  private UnderNotice underNotice;
  private String sanction;
  private RecommendationStatus doctorStatus;
  private LocalDate lastUpdatedDate;
  private String designatedBodyCode;
  private String admin;
  @Nullable
  Boolean syncEnd;

  public final static DoctorsForDB convert(final DoctorsForDbDto doctorsForDBDTO) {
    return DoctorsForDB.builder()
        .gmcReferenceNumber(doctorsForDBDTO.getGmcReferenceNumber())
        .doctorFirstName(doctorsForDBDTO.getDoctorFirstName())
        .doctorLastName(doctorsForDBDTO.getDoctorLastName())
        .submissionDate(convertGmcDateToLocalDate(doctorsForDBDTO.getSubmissionDate()))
        .dateAdded(convertGmcDateToLocalDate(doctorsForDBDTO.getDateAdded()))
        .underNotice(UnderNotice.fromString(doctorsForDBDTO.getUnderNotice()))
        .sanction(doctorsForDBDTO.getSanction())
        .doctorStatus(NOT_STARTED)
        .designatedBodyCode(doctorsForDBDTO.getDesignatedBodyCode())
        .lastUpdatedDate(now())
        .build();
  }
}

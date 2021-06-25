package uk.nhs.hee.tis.revalidation.entity;


import static java.time.LocalDate.now;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationStatus.NOT_STARTED;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.convertGmcDateToLocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
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

  @Nullable
  Boolean syncEnd;
  @Id
  private String gmcReferenceNumber;
  private String doctorFirstName;
  private String doctorLastName;
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate submissionDate;
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate dateAdded;
  private UnderNotice underNotice;
  private String sanction;
  private RecommendationStatus doctorStatus;
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate lastUpdatedDate;
  private String designatedBodyCode;
  private String admin;

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

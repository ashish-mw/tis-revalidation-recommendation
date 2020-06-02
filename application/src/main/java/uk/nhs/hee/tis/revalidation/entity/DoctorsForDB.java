package uk.nhs.hee.tis.revalidation.entity;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.nhs.hee.tis.revalidation.dto.DoctorsForDbDto;

import java.time.LocalDate;

import static java.time.LocalDate.now;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationStatus.NOT_STARTED;

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

    public final static DoctorsForDB convert(final DoctorsForDbDto doctorsForDBDTO) {
        return DoctorsForDB.builder()
                .gmcReferenceNumber(doctorsForDBDTO.getGmcReferenceNumber())
                .doctorFirstName(doctorsForDBDTO.getDoctorFirstName())
                .doctorLastName(doctorsForDBDTO.getDoctorLastName())
                .submissionDate(doctorsForDBDTO.getSubmissionDate())
                .dateAdded(doctorsForDBDTO.getDateAdded())
                .underNotice(UnderNotice.fromString(doctorsForDBDTO.getUnderNotice()))
                .sanction(doctorsForDBDTO.getSanction())
                .doctorStatus(NOT_STARTED)
                .designatedBodyCode(doctorsForDBDTO.getDesignatedBodyCode())
                .lastUpdatedDate(now())
                .build();
    }
}

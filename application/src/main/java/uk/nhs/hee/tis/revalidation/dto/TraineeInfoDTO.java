package uk.nhs.hee.tis.revalidation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;

import java.time.LocalDate;

import static java.time.LocalDate.now;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TraineeInfoDTO {

    private String gmcReferenceNumber;
    private String doctorFirstName;
    private String doctorLastName;
    private LocalDate submissionDate;
    private LocalDate dateAdded;
    private UnderNotice underNotice;
    private String sanction;

    //TODO: defaulting the values for F.E, Will be replace with proper values when Tis data available.
    @Builder.Default
    private String doctorStatus = "";
    @Builder.Default
    private String programmeName = "";
    @Builder.Default
    private String programmeMembershipType = "";
    @Builder.Default
    private LocalDate cctDate = now();
    @Builder.Default
    private String admin = "";
    @Builder.Default
    private LocalDate lastUpdatedDate = now();
}

package uk.nhs.hee.tis.revalidation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private String underNotice;
    private String sanction;
    private String doctorStatus;
    private String programmeName;
    private String programmeMembershipType;
    private LocalDate cctDate;
    private String currentGrade;
    private String admin;
    private LocalDate lastUpdatedDate;
}

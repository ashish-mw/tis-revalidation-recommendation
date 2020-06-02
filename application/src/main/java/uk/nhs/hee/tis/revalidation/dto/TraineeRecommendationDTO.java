package uk.nhs.hee.tis.revalidation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown =  true)
public class TraineeRecommendationDTO {

    private String fullName;
    private String gmcNumber;
    private String programmeMembershipType;
    private String currentGrade;
    private LocalDate cctDate;
    private String underNotice;
    private List<TraineeRecommendationRecordDTO> revalidations;
    private List<DeferralReasonDTO> deferralReasons;

}

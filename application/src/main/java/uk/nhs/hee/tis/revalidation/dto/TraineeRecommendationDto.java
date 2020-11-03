package uk.nhs.hee.tis.revalidation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
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
@ApiModel(description = "Trainee recommendation information")
public class TraineeRecommendationDto {

    private String fullName;
    private String gmcNumber;
    private String programmeMembershipType;
    private String currentGrade;
    private LocalDate cctDate;
    private String underNotice;
    private String designatedBody;
    private LocalDate gmcSubmissionDate;
    private List<TraineeRecommendationRecordDto> revalidations;
    private List<DeferralReasonDto> deferralReasons;

}

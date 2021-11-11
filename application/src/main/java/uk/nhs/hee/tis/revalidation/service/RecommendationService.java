package uk.nhs.hee.tis.revalidation.service;

import uk.nhs.hee.tis.revalidation.dto.RoUserProfileDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationRecordDto;
import uk.nhs.hee.tis.revalidation.entity.Recommendation;
import uk.nhs.hee.tis.revalidation.entity.RecommendationStatus;

import java.util.List;
import java.util.Map;

public interface RecommendationService {
    //get trainee information with current and legacy recommendations
    TraineeRecommendationDto getTraineeInfo(String gmcId);

    //save a new recommendation
    Recommendation saveRecommendation(TraineeRecommendationRecordDto recordDTO);

    //update an existing recommendation
    Recommendation updateRecommendation(TraineeRecommendationRecordDto recordDTO);

    //submit a recommendation to gmc
    boolean submitRecommendation(String recommendationId, String gmcNumber,
                                         RoUserProfileDto userProfileDto);

    //get latest recommendations of a trainee
    TraineeRecommendationRecordDto getLatestRecommendation(String gmcId);

    //get latest recommendations of a list of trainees
    Map<String, TraineeRecommendationRecordDto> getLatestRecommendations(
            List<String> gmcIds);

    RecommendationStatus getRecommendationStatusForTrainee(String gmcId);
}

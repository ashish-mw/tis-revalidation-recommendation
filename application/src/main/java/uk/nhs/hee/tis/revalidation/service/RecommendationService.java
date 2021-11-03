package uk.nhs.hee.tis.revalidation.service;

import uk.nhs.hee.tis.revalidation.dto.RoUserProfileDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationDto;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationRecordDto;
import uk.nhs.hee.tis.revalidation.entity.Recommendation;
import uk.nhs.hee.tis.revalidation.entity.RecommendationStatus;
import uk.nhs.hee.tis.revalidation.entity.RecommendationType;
import uk.nhs.hee.tis.revalidation.exception.RecommendationException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.time.LocalDate.now;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.SUCCESS;
import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.fromCode;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome.*;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationStatus.READY_TO_REVIEW;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationStatus.SUBMITTED_TO_GMC;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationType.NON_ENGAGEMENT;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationType.REVALIDATE;

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

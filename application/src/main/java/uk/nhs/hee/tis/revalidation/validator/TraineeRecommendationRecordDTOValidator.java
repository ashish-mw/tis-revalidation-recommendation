package uk.nhs.hee.tis.revalidation.validator;

import static java.time.LocalDate.now;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationRecordDto;
import uk.nhs.hee.tis.revalidation.entity.RecommendationType;

public class TraineeRecommendationRecordDTOValidator implements Validator {

  public static final String INSUFFICIENT_EVIDENCE = "1";

  @Override
  public boolean supports(Class<?> aClass) {
    return TraineeRecommendationRecordDto.class.isAssignableFrom(aClass);
  }

  //validate TraineeRecommendationRecordDTO for the creation of new Recommendation
  @Override
  public void validate(Object target, Errors errors) {

    if (errors.getErrorCount() == 0) {
      final var recordDTO = (TraineeRecommendationRecordDto) target;
      if (recordDTO.getGmcNumber() == null || "".equals(recordDTO.getGmcNumber())) {
        errors.reject("GmcNumber", "Gmc Number can't be empty or null");
      }
      if (recordDTO.getRecommendationType() == null || "".equals(recordDTO.getRecommendationType())) {
        errors.reject("RecommendationType", "Recommendation type can't be empty or null");
      } else {
        final var recommendationType = RecommendationType
            .valueOf(recordDTO.getRecommendationType());
        if (RecommendationType.DEFER.equals(recommendationType)) {
          if (recordDTO.getDeferralDate() == null || recordDTO.getDeferralDate().isBefore(now())) {
            errors.reject("DeferralDate", "Deferral date can't be empty or in past");
          }
          if (recordDTO.getDeferralReason() == null || "".equals(recordDTO.getDeferralReason())) {
            errors.reject("DeferralReason", "Deferral Reason can't be empty or null");
          } else if (recordDTO.getDeferralReason().equalsIgnoreCase(INSUFFICIENT_EVIDENCE)
              && (recordDTO.getDeferralSubReason() == null || "".equals(recordDTO.getDeferralSubReason()))) {
            errors.reject("DeferralSubReason", "Deferral Sub Reason can't be empty or null");
          }
        }
      }
    }
  }
}
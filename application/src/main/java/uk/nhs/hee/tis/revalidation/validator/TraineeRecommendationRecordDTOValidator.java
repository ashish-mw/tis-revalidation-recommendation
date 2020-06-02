package uk.nhs.hee.tis.revalidation.validator;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationRecordDTO;
import uk.nhs.hee.tis.revalidation.entity.RecommendationType;

public class TraineeRecommendationRecordDTOValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return TraineeRecommendationRecordDTO.class.isAssignableFrom(aClass);
    }

    //validate TraineeRecommendationRecordDTO for the creation of new Recommendation
    @Override
    public void validate(Object target, Errors errors) {

        if (errors.getErrorCount() == 0) {
            final var recordDTO = (TraineeRecommendationRecordDTO) target;
            if (StringUtils.isEmpty(recordDTO.getGmcNumber())) {
                errors.reject("GmcNumber","Gmc Number can't be empty or null");
            }
            if (StringUtils.isEmpty(recordDTO.getRecommendationType())) {
                errors.reject("RecommendationType","Recommendation type can't be empty or null");
            } else {
                final var recommendationType = RecommendationType.valueOf(recordDTO.getRecommendationType());
                if (RecommendationType.DEFER.equals(recommendationType)) {
                    if (recordDTO.getDeferralDate() == null) {
                        errors.reject("DeferralDate","Deferral date can't be empty or null");
                    }
                    if (StringUtils.isEmpty(recordDTO.getDeferralReason())) {
                        errors.reject("DeferralReason", "Deferral Reason can't be empty or null");
                    }
                }
            }

        }
    }
}

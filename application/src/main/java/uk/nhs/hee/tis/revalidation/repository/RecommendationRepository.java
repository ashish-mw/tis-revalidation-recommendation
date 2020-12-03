package uk.nhs.hee.tis.revalidation.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.hee.tis.revalidation.entity.Recommendation;
import uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome;

import java.util.List;

import static uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome.*;

@Repository
public interface RecommendationRepository extends MongoRepository<Recommendation, String> {

    Recommendation findByIdAndGmcNumber(final String id, final String gmcNumber);

    Optional<Recommendation> findFirstByGmcNumberOrderByActualSubmissionDateDesc(final String gmcNumber);

    //get recommendation which can be update, APPROVED and REJECTED recommendation cannot be update and will be fetch from snapshot
    default List<Recommendation> findByGmcNumber(final String gmcNumber) {
        return findAllByGmcNumberAndOutcomeNotIn(gmcNumber, APPROVED, REJECTED);
    }

    List<Recommendation> findAllByGmcNumberAndOutcomeNotIn(final String gmcNumber, final RecommendationGmcOutcome... outcome);
}

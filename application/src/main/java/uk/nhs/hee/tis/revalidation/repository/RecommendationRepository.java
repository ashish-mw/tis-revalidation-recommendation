package uk.nhs.hee.tis.revalidation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.hee.tis.revalidation.entity.Recommendation;

@Repository
public interface RecommendationRepository extends MongoRepository<Recommendation, String> {

    Recommendation findByIdAndGmcNumber(final String id, final String gmcNumber);
}

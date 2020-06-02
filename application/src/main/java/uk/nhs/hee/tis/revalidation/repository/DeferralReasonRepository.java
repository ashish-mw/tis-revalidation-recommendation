package uk.nhs.hee.tis.revalidation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.hee.tis.revalidation.entity.DeferralReason;

@Repository
public interface DeferralReasonRepository extends MongoRepository<DeferralReason, String> {

}

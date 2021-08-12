package uk.nhs.hee.tis.revalidation.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.hee.tis.revalidation.entity.DeferralReason;
import uk.nhs.hee.tis.revalidation.entity.Status;

@Repository
public interface DeferralReasonRepository extends MongoRepository<DeferralReason, String> {

  List<DeferralReason> findAllByStatus(final Status status);
}

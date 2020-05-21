package uk.nhs.hee.tis.revalidation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.hee.tis.revalidation.entity.Snapshot;

import java.util.List;

@Repository
public interface SnapshotRepository extends MongoRepository<Snapshot, String> {

    List<Snapshot> findByGmcNumber(final String gmcNumber);
}

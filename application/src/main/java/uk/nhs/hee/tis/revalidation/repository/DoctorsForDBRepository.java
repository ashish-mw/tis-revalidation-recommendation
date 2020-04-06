package uk.nhs.hee.tis.revalidation.repository;


import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;

import java.util.List;

@Repository
public interface DoctorsForDBRepository extends MongoRepository<DoctorsForDB, String> {

    //Get count for trainee doctors who are underNotice
    long countByUnderNoticeIn(final String... underNotice);

    //Get trainee doctors who are underNotice
    List<DoctorsForDB> findAllByUnderNoticeIn(final Sort sort, final String... underNotice);
}

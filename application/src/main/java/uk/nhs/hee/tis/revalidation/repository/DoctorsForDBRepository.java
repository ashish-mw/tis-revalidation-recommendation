package uk.nhs.hee.tis.revalidation.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;

@Repository
public interface DoctorsForDBRepository extends MongoRepository<DoctorsForDB, String> {

    //Get count for trainee doctors who are underNotice (every one but with UnderNotice value of No)
    long countByUnderNoticeIsNot(final String underNotice);
}

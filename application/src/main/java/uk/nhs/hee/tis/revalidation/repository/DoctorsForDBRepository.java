package uk.nhs.hee.tis.revalidation.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;

@Repository
public interface DoctorsForDBRepository extends MongoRepository<DoctorsForDB, String> {

    //Get count for trainee doctors who are underNotice
    long countByUnderNoticeIn(final String... underNotice);

    //Get trainee doctors who are underNotice
    Page<DoctorsForDB> findAllByUnderNoticeIn(final Pageable pageable, final String... underNotice);
}

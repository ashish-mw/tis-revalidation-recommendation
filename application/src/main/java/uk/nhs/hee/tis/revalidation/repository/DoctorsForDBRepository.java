package uk.nhs.hee.tis.revalidation.repository;


import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;

@Repository
public interface DoctorsForDBRepository extends MongoRepository<DoctorsForDB, String> {

  //Get count for trainee doctors who are underNotice
  long countByUnderNoticeIn(final UnderNotice... underNotice);

  @Query(value = "{ '$and' : [{ '$or' : [{'doctorFirstName' : { '$regex' : ?0, '$options' : 'i'}}, "
      + "{ 'doctorLastName' : { '$regex' : ?0, '$options' : 'i'}}, { '_id' : { '$regex' : ?0, '$options' : 'i'}}]}, "
      + "{ 'designatedBodyCode' : { '$in' : ?1 }}, { 'underNotice' : { '$in' : ?2 }}]}")
  Page<DoctorsForDB> findByUnderNotice(final Pageable pageable, final String searchQuery,
      final List<String> dbcs, final UnderNotice... underNotice);

  @Query(value = "{ '$and' : [{ '$or' : [{'doctorFirstName' : { '$regex' : ?0, '$options' : 'i'}}, "
      + "{ 'doctorLastName' : { '$regex' : ?0, '$options' : 'i'}}, { '_id' : { '$regex' : ?0, '$options' : 'i'}}]}, { 'designatedBodyCode' : { '$in' : ?1 }}, { '_id' : { '$nin' : ?2 }}]}")
  Page<DoctorsForDB> findAll(final Pageable pageable, final String searchQuery, final List<String> dbcs, final List<String> hiddenGmcIds);

}

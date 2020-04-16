package uk.nhs.hee.tis.revalidation.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;

import java.util.List;

import static java.util.List.of;

@Repository
public interface DoctorsForDBRepository extends MongoRepository<DoctorsForDB, String> {

    //Get count for trainee doctors who are underNotice
    long countByUnderNoticeIn(final UnderNotice... underNotice);

    //Get trainee doctors as part of search results (search by first name or last name or gmc number).
    //TODO: look at the query option too
    Page<DoctorsForDB> findByDoctorFirstNameIgnoreCaseLikeOrDoctorLastNameIgnoreCaseLikeOrGmcReferenceNumberIgnoreCaseLike(final Pageable pageable,
                                                                                                                           final String firstName,
                                                                                                                           final String lastName,
                                                                                                                           final String gmcNumber);

    //Get under notice trainee doctors as part of search results (search by first name or last name or gmc number).
    //TODO: look at the query option too
    Page<DoctorsForDB> findByDoctorFirstNameIgnoreCaseLikeAndUnderNoticeInOrDoctorLastNameIgnoreCaseLikeAndUnderNoticeInOrGmcReferenceNumberIgnoreCaseLikeAndUnderNoticeIn(final Pageable pageable,
                                                                                                                                                                           final String firstName,
                                                                                                                                                                           final List<UnderNotice> un1,
                                                                                                                                                                           final String lastName,
                                                                                                                                                                           final List<UnderNotice> un2,
                                                                                                                                                                           final String gmcNumber,
                                                                                                                                                                           final List<UnderNotice> un3);


    //A wrapper search method of doctors to make it easy for use.
    default Page<DoctorsForDB> findAll(final Pageable pageable, final String searchQuery) {
        return findByDoctorFirstNameIgnoreCaseLikeOrDoctorLastNameIgnoreCaseLikeOrGmcReferenceNumberIgnoreCaseLike(pageable,
                searchQuery,
                searchQuery,
                searchQuery);
    }

    //A wrapper search method of under notice doctors to make it easy for use.
    default Page<DoctorsForDB> findAllByUnderNoticeIn(final Pageable pageable, final String searchQuery, final UnderNotice... underNotice) {
        return findByDoctorFirstNameIgnoreCaseLikeAndUnderNoticeInOrDoctorLastNameIgnoreCaseLikeAndUnderNoticeInOrGmcReferenceNumberIgnoreCaseLikeAndUnderNoticeIn(pageable,
                searchQuery,
                of(underNotice),
                searchQuery,
                of(underNotice),
                searchQuery,
                of(underNotice));
    }

}

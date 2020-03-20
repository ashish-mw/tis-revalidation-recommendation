package uk.nhs.hee.tis.revalidation.service;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;

import java.time.LocalDate;

import static java.util.List.of;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class DoctorsForDBServiceTest {

    private final Faker faker = new Faker();

    @InjectMocks
    private DoctorsForDBService doctorsForDBService;

    @Mock
    private DoctorsForDBRepository repository;

    private DoctorsForDB doc1, doc2, doc3, doc4, doc5;
    private String gmcRef1, gmcRef2, gmcRef3, gmcRef4, gmcRef5;
    private String fname1, fname2, fname3, fname4, fname5;
    private String lname1, lname2, lname3, lname4, lname5;
    private LocalDate subDate1, subDate2, subDate3, subDate4, subDate5;
    private LocalDate addedDate1, addedDate2, addedDate3, addedDate4, addedDate5;
    private UnderNotice un1, un2, un3, un4, un5;
    private String sanction1, sanction2, sanction3, sanction4,sanction5;

    @Before
    public void setup() {
        setupData();
    }

    @Test
    public void shouldReturnListOfDoctors() {

        when(repository.findAll()).thenReturn(of(doc1, doc2, doc3, doc4, doc5));
        var allDoctors = doctorsForDBService.getAllTraineeDoctorDetails();
        var doctorsForDB = allDoctors.getDoctorsForDB();
        assertThat(doctorsForDB, hasSize(5));

        assertThat(doctorsForDB.get(0).getGmcReferenceNumber(), is(gmcRef1));
        assertThat(doctorsForDB.get(0).getDoctorFirstName(), is(fname1));
        assertThat(doctorsForDB.get(0).getDoctorLastName(), is(lname1));
        assertThat(doctorsForDB.get(0).getSubmissionDate(), is(subDate1));
        assertThat(doctorsForDB.get(0).getDateAdded(), is(addedDate1));
        assertThat(doctorsForDB.get(0).getUnderNotice(), is(un1.value()));
        assertThat(doctorsForDB.get(0).getSanction(), is(sanction1));

        assertThat(doctorsForDB.get(1).getGmcReferenceNumber(), is(gmcRef2));
        assertThat(doctorsForDB.get(1).getDoctorFirstName(), is(fname2));
        assertThat(doctorsForDB.get(1).getDoctorLastName(), is(lname2));
        assertThat(doctorsForDB.get(1).getSubmissionDate(), is(subDate2));
        assertThat(doctorsForDB.get(1).getDateAdded(), is(addedDate2));
        assertThat(doctorsForDB.get(1).getUnderNotice(), is(un2.value()));
        assertThat(doctorsForDB.get(1).getSanction(), is(sanction2));

        assertThat(doctorsForDB.get(2).getGmcReferenceNumber(), is(gmcRef3));
        assertThat(doctorsForDB.get(2).getDoctorFirstName(), is(fname3));
        assertThat(doctorsForDB.get(2).getDoctorLastName(), is(lname3));
        assertThat(doctorsForDB.get(2).getSubmissionDate(), is(subDate3));
        assertThat(doctorsForDB.get(2).getDateAdded(), is(addedDate3));
        assertThat(doctorsForDB.get(2).getUnderNotice(), is(un3.value()));
        assertThat(doctorsForDB.get(2).getSanction(), is(sanction3));

        assertThat(doctorsForDB.get(3).getGmcReferenceNumber(), is(gmcRef4));
        assertThat(doctorsForDB.get(3).getDoctorFirstName(), is(fname4));
        assertThat(doctorsForDB.get(3).getDoctorLastName(), is(lname4));
        assertThat(doctorsForDB.get(3).getSubmissionDate(), is(subDate4));
        assertThat(doctorsForDB.get(3).getDateAdded(), is(addedDate4));
        assertThat(doctorsForDB.get(3).getUnderNotice(), is(un4.value()));
        assertThat(doctorsForDB.get(3).getSanction(), is(sanction4));

        assertThat(doctorsForDB.get(4).getGmcReferenceNumber(), is(gmcRef5));
        assertThat(doctorsForDB.get(4).getDoctorFirstName(), is(fname5));
        assertThat(doctorsForDB.get(4).getDoctorLastName(), is(lname5));
        assertThat(doctorsForDB.get(4).getSubmissionDate(), is(subDate5));
        assertThat(doctorsForDB.get(4).getDateAdded(), is(addedDate5));
        assertThat(doctorsForDB.get(4).getUnderNotice(), is(un5.value()));
        assertThat(doctorsForDB.get(4).getSanction(), is(sanction5));
    }

    private void setupData() {
        gmcRef1 = faker.number().digits(8);
        gmcRef2 = faker.number().digits(8);
        gmcRef3 = faker.number().digits(8);
        gmcRef4 = faker.number().digits(8);
        gmcRef5 = faker.number().digits(8);

        fname1 = faker.name().firstName();
        fname2 = faker.name().firstName();
        fname3 = faker.name().firstName();
        fname4 = faker.name().firstName();
        fname5 = faker.name().firstName();

        lname1 = faker.name().lastName();
        lname2 = faker.name().lastName();
        lname3 = faker.name().lastName();
        lname4 = faker.name().lastName();
        lname5 = faker.name().lastName();

        subDate1 = LocalDate.now();
        subDate2 = LocalDate.now();
        subDate3 = LocalDate.now();
        subDate4 = LocalDate.now();
        subDate5 = LocalDate.now();

        addedDate1 = LocalDate.now().minusDays(5);
        addedDate2 = LocalDate.now().minusDays(5);
        addedDate3 = LocalDate.now().minusDays(5);
        addedDate4 = LocalDate.now().minusDays(5);
        addedDate5 = LocalDate.now().minusDays(5);

        un1 = faker.options().option(UnderNotice.class);
        un2 = faker.options().option(UnderNotice.class);
        un3 = faker.options().option(UnderNotice.class);
        un4 = faker.options().option(UnderNotice.class);
        un5 = faker.options().option(UnderNotice.class);

        sanction1 = faker.lorem().characters(2);
        sanction2 = faker.lorem().characters(2);
        sanction3 = faker.lorem().characters(2);
        sanction4 = faker.lorem().characters(2);
        sanction5 = faker.lorem().characters(2);

        doc1 = new DoctorsForDB(gmcRef1, fname1, lname1, subDate1, addedDate1, un1, sanction1);
        doc2 = new DoctorsForDB(gmcRef2, fname2, lname2, subDate2, addedDate2, un2, sanction2);
        doc3 = new DoctorsForDB(gmcRef3, fname3, lname3, subDate3, addedDate3, un3, sanction3);
        doc4 = new DoctorsForDB(gmcRef4, fname4, lname4, subDate4, addedDate4, un4, sanction4);
        doc5 = new DoctorsForDB(gmcRef5, fname5, lname5, subDate5, addedDate5, un5, sanction5);
    }
}
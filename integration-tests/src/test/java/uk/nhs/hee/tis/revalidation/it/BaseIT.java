package uk.nhs.hee.tis.revalidation.it;

import com.github.javafaker.Faker;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;

import java.time.LocalDate;

import static java.time.LocalDate.now;

public class BaseIT {

    private final Faker faker = new Faker();

    protected DoctorsForDB doc1, doc2, doc3, doc4, doc5;
    protected String gmcRef1, gmcRef2, gmcRef3, gmcRef4, gmcRef5;
    protected String fName1, fName2, fName3, fName4, fName5;
    protected String lName1, lName2, lName3, lName4, lName5;
    protected LocalDate subDate1, subDate2, subDate3, subDate4, subDate5;
    protected LocalDate addedDate1, addedDate2, addedDate3, addedDate4, addedDate5;
    protected UnderNotice un1, un2, un3, un4, un5;
    protected String sanction1, sanction2, sanction3, sanction4, sanction5;
    protected String status1, status2, status3, status4, status5;

    protected void setupData() {
        gmcRef1 = faker.number().digits(8);
        gmcRef2 = faker.number().digits(8);
        gmcRef3 = faker.number().digits(8);
        gmcRef4 = faker.number().digits(8);
        gmcRef5 = faker.number().digits(8);

        fName1 = faker.name().firstName();
        fName2 = faker.name().firstName();
        fName3 = faker.name().firstName();
        fName4 = faker.name().firstName();
        fName5 = faker.name().firstName();

        lName1 = faker.name().lastName();
        lName2 = faker.name().lastName();
        lName3 = faker.name().lastName();
        lName4 = faker.name().lastName();
        lName5 = faker.name().lastName();

        subDate1 = now();
        subDate2 = now();
        subDate3 = now();
        subDate4 = now();
        subDate5 = now();

        addedDate1 = now().minusDays(5);
        addedDate2 = now().minusDays(5);
        addedDate3 = now().minusDays(5);
        addedDate4 = now().minusDays(5);
        addedDate5 = now().minusDays(5);

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

        status1 = faker.lorem().characters(10);
        status2 = faker.lorem().characters(10);
        status3 = faker.lorem().characters(10);
        status4 = faker.lorem().characters(10);
        status5 = faker.lorem().characters(10);

        doc1 = new DoctorsForDB(gmcRef1, fName1, lName1, subDate1, addedDate1, un1, sanction1, status1);
        doc2 = new DoctorsForDB(gmcRef2, fName2, lName2, subDate2, addedDate2, un2, sanction2, status2);
        doc3 = new DoctorsForDB(gmcRef3, fName3, lName3, subDate3, addedDate3, un3, sanction3, status3);
        doc4 = new DoctorsForDB(gmcRef4, fName4, lName4, subDate4, addedDate4, un4, sanction4, status4);
        doc5 = new DoctorsForDB(gmcRef5, fName5, lName5, subDate5, addedDate5, un5, sanction5, status5);
    }
}

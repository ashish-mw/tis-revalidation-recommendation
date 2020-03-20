package uk.nhs.hee.tis.revalidation.aggregate;

import com.github.javafaker.Faker;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;
import uk.nhs.hee.tis.revalidation.command.DoctorForDBReceivedCommand;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.event.DoctorsForDBReceivedEvent;

import java.time.LocalDate;

public class DoctorsForDBAggregateTest {

    private FixtureConfiguration<DoctorsForDBAggregate> fixture;

    private Faker faker = new Faker();
    private String gmcReference;
    private String firstName;
    private String lastName;
    private LocalDate submissionDate;
    private LocalDate dateAdded;
    private UnderNotice underNotice;
    private String sanction;

    @Before
    public void setup() {
        fixture = new AggregateTestFixture<>(DoctorsForDBAggregate.class);
        gmcReference = faker.number().digits(8);
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        submissionDate = LocalDate.now();
        dateAdded = LocalDate.now().minusDays(5);
        underNotice = faker.options().option(UnderNotice.class);
        sanction = faker.lorem().characters(2);
    }

    @Test
    public void testCommandHandlerInAggregate() {
        final var command = DoctorForDBReceivedCommand.builder()
                .gmcReferenceNumber(gmcReference)
                .doctorFirstName(firstName)
                .doctorLastName(lastName)
                .submissionDate(submissionDate)
                .dateAdded(dateAdded)
                .underNotice(underNotice.value())
                .sanction(sanction)
                .build();

        final var event = DoctorsForDBReceivedEvent.builder()
                .gmcReferenceNumber(gmcReference)
                .doctorFirstName(firstName)
                .doctorLastName(lastName)
                .submissionDate(submissionDate)
                .dateAdded(dateAdded)
                .underNotice(underNotice)
                .sanction(sanction)
                .build();

        fixture.given().when(command).expectSuccessfulHandlerExecution()
                .expectEvents(event);

    }

}
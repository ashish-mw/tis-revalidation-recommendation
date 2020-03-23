package uk.nhs.hee.tis.revalidation.it;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.hee.tis.revalidation.RevalidationApplication;
import uk.nhs.hee.tis.revalidation.dto.DoctorsForDBDTO;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.time.LocalDate.now;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RevalidationApplication.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
public class RevalidationIT {

    private final Faker faker = new Faker();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DoctorsForDBRepository repository;

    @Value("${app.rabbit.routingKey}")
    private String routingKey;

    @Value("${app.rabbit.exchange}")
    private String exchange;

    private String gmcReference;
    private String firstName;
    private String lastName;
    private LocalDate submissionDate;
    private LocalDate dateAdded;
    private UnderNotice underNotice;
    private String sanction;

    @DisplayName("Given a message arrive in queue, when revalidation recieve it, then store it in database")
    @Test
    public void shouldReceiveDoctorInformationAndAddIntoDatabase() throws InterruptedException {

        gmcReference = faker.number().digits(8);
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        submissionDate = now();
        dateAdded = now().minusDays(5);
        underNotice = faker.options().option(UnderNotice.class);
        sanction = faker.lorem().characters(2);

        final var gmcDoctorDTO = DoctorsForDBDTO.builder()
                .gmcReferenceNumber(gmcReference)
                .doctorFirstName(firstName)
                .doctorLastName(lastName)
                .submissionDate(submissionDate)
                .dateAdded(dateAdded)
                .underNotice(underNotice.value())
                .sanction(sanction)
                .build();

        rabbitTemplate.convertAndSend(exchange, routingKey, gmcDoctorDTO);

        TimeUnit.SECONDS.sleep(5);

        final Optional<DoctorsForDB> byId = repository.findById(gmcReference);

        assertThat(byId.isPresent(), is(true));
        assertThat(byId.get().getGmcReferenceNumber(), is(gmcReference));
        assertThat(byId.get().getDoctorFirstName(), is(firstName));
        assertThat(byId.get().getDoctorLastName(), is(lastName));
        assertThat(byId.get().getSubmissionDate(), is(submissionDate));
        assertThat(byId.get().getDateAdded(), is(dateAdded));
        assertThat(byId.get().getUnderNotice(), is(underNotice));
        assertThat(byId.get().getSanction(), is(sanction));
    }
}

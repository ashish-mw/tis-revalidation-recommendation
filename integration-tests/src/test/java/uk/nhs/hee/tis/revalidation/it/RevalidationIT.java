/*
 * The MIT License (MIT)
 *
 * Copyright 2021 Crown Copyright (Health Education England)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.nhs.hee.tis.revalidation.it;

import static java.time.LocalDate.now;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.convertDateInGmcFormat;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.hee.tis.revalidation.RevalidationApplication;
import uk.nhs.hee.tis.revalidation.dto.DoctorsForDbDto;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.repository.DoctorsForDBRepository;

@Slf4j
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = RevalidationApplication.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
class RevalidationIT {

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
  void shouldReceiveDoctorInformationAndAddIntoDatabase() throws InterruptedException {

    gmcReference = faker.number().digits(8);
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();
    submissionDate = now();
    dateAdded = now().minusDays(5);
    underNotice = faker.options().option(UnderNotice.class);
    sanction = faker.lorem().characters(2);

    final var gmcDoctorDTO = DoctorsForDbDto.builder()
        .gmcReferenceNumber(gmcReference)
        .doctorFirstName(firstName)
        .doctorLastName(lastName)
        .submissionDate(convertDateInGmcFormat(submissionDate))
        .dateAdded(convertDateInGmcFormat(dateAdded))
        .underNotice(underNotice.value())
        .sanction(sanction)
        .build();

    rabbitTemplate.convertAndSend(exchange, routingKey, gmcDoctorDTO);

    SECONDS.sleep(5);

    final var doctorsForDB = repository.findById(gmcReference);

    assertThat(doctorsForDB.isPresent(), is(true));
    assertThat(doctorsForDB.get().getGmcReferenceNumber(), is(gmcReference));
    assertThat(doctorsForDB.get().getDoctorFirstName(), is(firstName));
    assertThat(doctorsForDB.get().getDoctorLastName(), is(lastName));
    assertThat(doctorsForDB.get().getSubmissionDate(), is(submissionDate));
    assertThat(doctorsForDB.get().getDateAdded(), is(dateAdded));
    assertThat(doctorsForDB.get().getUnderNotice(), is(underNotice));
    assertThat(doctorsForDB.get().getSanction(), is(sanction));
  }
}

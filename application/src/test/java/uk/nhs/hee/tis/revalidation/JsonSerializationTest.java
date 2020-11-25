package uk.nhs.hee.tis.revalidation;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.nhs.hee.tis.revalidation.controller.DoctorsForDBController;
import uk.nhs.hee.tis.revalidation.dto.DoctorsForDbDto;
import uk.nhs.hee.tis.revalidation.service.DoctorsForDBService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(DoctorsForDBController.class)
public class JsonSerializationTest {

  @MockBean
  private DoctorsForDBService mDoctorsForDbService;

  @Autowired
  private ObjectMapper mapper;

  @Test
  public void testDateSerialization() throws JsonProcessingException {
    assertThat(mapper, is(notNullValue()));

    final var doctor = DoctorsForDbDto.builder()
        .doctorFirstName("first")
        .doctorLastName("last")
        .gmcReferenceNumber("gmtRef")
        .sanction("sanction")
        .underNotice("under notice")
        .dateAdded("04/07/2017")
        .submissionDate("04/07/2017").build();

    final var json = mapper.writeValueAsString(doctor);

    assertThat(json,
        is("{\"gmcReferenceNumber\":\"gmtRef\",\"doctorFirstName\":\"first\",\"doctorLastName\":\"last\",\"submissionDate\":\"04/07/2017\",\"dateAdded\":\"04/07/2017\",\"underNotice\":\"under notice\",\"sanction\":\"sanction\",\"designatedBodyCode\":null}"));
  }

}

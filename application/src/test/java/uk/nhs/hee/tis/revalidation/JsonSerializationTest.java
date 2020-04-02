package uk.nhs.hee.tis.revalidation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.hee.tis.revalidation.controller.DoctorsForDBController;
import uk.nhs.hee.tis.revalidation.dto.DoctorsForDBDTO;
import uk.nhs.hee.tis.revalidation.service.DoctorsForDBService;

import static java.time.LocalDate.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@WebMvcTest(DoctorsForDBController.class)
public class JsonSerializationTest {

    @MockBean
    private DoctorsForDBService mDoctorsForDbService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testDateSerialization() throws JsonProcessingException {
        assertThat(mapper, is(notNullValue()));

        final var doctor = DoctorsForDBDTO.builder()
                .doctorFirstName("first")
                .doctorLastName("last")
                .gmcReferenceNumber("gmtRef")
                .sanction("sanction")
                .underNotice("under notice")
                .dateAdded(of(2020, 4, 2))
                .submissionDate(of(2020,3,31)).build();

        final var json = mapper.writeValueAsString(doctor);

        assertThat(json,is("{\"gmcReferenceNumber\":\"gmtRef\",\"doctorFirstName\":\"first\",\"doctorLastName\":\"last\",\"submissionDate\":\"2020-03-31\",\"dateAdded\":\"2020-04-02\",\"underNotice\":\"under notice\",\"sanction\":\"sanction\"}"));
    }

}

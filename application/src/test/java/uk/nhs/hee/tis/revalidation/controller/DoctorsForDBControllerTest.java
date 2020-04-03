package uk.nhs.hee.tis.revalidation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.hee.tis.revalidation.dto.TraineeDoctorDTO;
import uk.nhs.hee.tis.revalidation.dto.RevalidationRequestDTO;
import uk.nhs.hee.tis.revalidation.dto.TraineeInfoDTO;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.service.DoctorsForDBService;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.List.of;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.nhs.hee.tis.revalidation.controller.DoctorsForDBController.*;

@RunWith(SpringRunner.class)
@WebMvcTest(DoctorsForDBController.class)
public class DoctorsForDBControllerTest {

    private static final String DOCTORS_API_URL = "/api/v1/doctors";
    private final Faker faker = new Faker();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private DoctorsForDBService doctorsForDBService;

    private String gmcReference;
    private String firstName;
    private String lastName;
    private LocalDate submissionDate;
    private LocalDate dateAdded;
    private UnderNotice underNotice;
    private String sanction;

    @Before
    public void setup() {
        gmcReference = faker.number().digits(8);
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        submissionDate = now();
        dateAdded = now().minusDays(5);
        underNotice = faker.options().option(UnderNotice.class);
        sanction = faker.lorem().characters(2);
    }

    @Test
    public void shouldReturnTraineeDoctorsInformation() throws Exception {
        final var gmcDoctorDTO = prepareGmcDoctor();
        final var requestDTO = RevalidationRequestDTO.builder().sortOrder(ASC).sortColumn(SUBMISSION_DATE).build();
        when(doctorsForDBService.getAllTraineeDoctorDetails(requestDTO)).thenReturn(gmcDoctorDTO);
        this.mockMvc.perform(get("/api/v1/doctors")
                .param(SORT_ORDER, ASC)
                .param(SORT_COLUMN, SUBMISSION_DATE))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(gmcDoctorDTO)));
    }

    @Test
    public void shouldReturnDataWhenSortOrderAndSortColumnAreEmpty() throws Exception {
        final var gmcDoctorDTO = prepareGmcDoctor();
        final var requestDTO = RevalidationRequestDTO.builder().sortOrder(DESC).sortColumn(SUBMISSION_DATE).build();
        when(doctorsForDBService.getAllTraineeDoctorDetails(requestDTO)).thenReturn(gmcDoctorDTO);
        this.mockMvc.perform(get("/api/v1/doctors")
                .param(SORT_ORDER, "")
                .param(SORT_COLUMN, ""))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(gmcDoctorDTO)));
    }

    @Test
    public void shouldReturnDataWhenSortOrderAndSortColumnAreInvalid() throws Exception {
        final var gmcDoctorDTO = prepareGmcDoctor();
        final var requestDTO = RevalidationRequestDTO.builder().sortOrder(DESC).sortColumn(SUBMISSION_DATE).build();
        when(doctorsForDBService.getAllTraineeDoctorDetails(requestDTO)).thenReturn(gmcDoctorDTO);
        this.mockMvc.perform(get(DOCTORS_API_URL)
                .param(SORT_ORDER, "aa")
                .param(SORT_COLUMN, "date"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(gmcDoctorDTO)));
    }

    private TraineeDoctorDTO prepareGmcDoctor() {
        final var doctorsForDB = buildDoctorsForDBList();
        return TraineeDoctorDTO.builder()
                .traineeInfo(doctorsForDB)
                .count(doctorsForDB.size())
                .build();
    }

    private List<TraineeInfoDTO> buildDoctorsForDBList() {
        final var doctor1 = TraineeInfoDTO.builder()
                .gmcReferenceNumber(gmcReference)
                .doctorFirstName(firstName)
                .doctorLastName(lastName)
                .submissionDate(submissionDate)
                .dateAdded(dateAdded)
                .underNotice(underNotice)
                .sanction(sanction)
                .build();
        return of(doctor1);
    }

}
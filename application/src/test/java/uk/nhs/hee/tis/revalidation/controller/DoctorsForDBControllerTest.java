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
import uk.nhs.hee.tis.revalidation.dto.RevalidationRequestDTO;
import uk.nhs.hee.tis.revalidation.dto.TraineeDoctorDTO;
import uk.nhs.hee.tis.revalidation.dto.TraineeInfoDTO;
import uk.nhs.hee.tis.revalidation.entity.RevalidationStatus;
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

    private String gmcRef1, gmcRef2;
    private String firstName1, firstName2;
    private String lastName1, lastName2;
    private LocalDate submissionDate1, submissionDate2;
    private LocalDate dateAdded1, dateAdded2;
    private UnderNotice underNotice1, underNotice2;
    private String sanction1, sanction2;
    private RevalidationStatus doctorStatus1, doctorStatus2;

    @Before
    public void setup() {
        gmcRef1 = faker.number().digits(8);
        gmcRef2 = faker.number().digits(8);
        firstName1 = faker.name().firstName();
        firstName2 = faker.name().firstName();
        lastName1 = faker.name().lastName();
        lastName2 = faker.name().lastName();
        submissionDate1 = now();
        submissionDate2 = now();
        dateAdded1 = now().minusDays(5);
        dateAdded2 = now().minusDays(5);
        underNotice1 = UnderNotice.YES;
        underNotice2 = UnderNotice.ON_HOLD;
        sanction1 = faker.lorem().characters(2);
        sanction2 = faker.lorem().characters(2);
        doctorStatus1 = RevalidationStatus.STARTED;
        doctorStatus2 = RevalidationStatus.SUBMITTED_TO_GMC;
    }

    @Test
    public void shouldReturnTraineeDoctorsInformation() throws Exception {
        final var gmcDoctorDTO = prepareGmcDoctor();
        final var requestDTO = RevalidationRequestDTO.builder().sortOrder(ASC).sortColumn(SUBMISSION_DATE).searchQuery(EMPTY_STRING).build();
        when(doctorsForDBService.getAllTraineeDoctorDetails(requestDTO)).thenReturn(gmcDoctorDTO);
        this.mockMvc.perform(get("/api/v1/doctors")
                .param(SORT_ORDER, ASC)
                .param(SORT_COLUMN, SUBMISSION_DATE)
                .param(UNDER_NOTICE, UNDER_NOTICE_VALUE)
                .param(PAGE_NUMBER, PAGE_NUMBER_VALUE)
                .param(SEARCH_QUERY, EMPTY_STRING))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(gmcDoctorDTO)));
    }

    @Test
    public void shouldReturnDataWhenSortOrderAndSortColumnAreEmpty() throws Exception {
        final var gmcDoctorDTO = prepareGmcDoctor();
        final var requestDTO = RevalidationRequestDTO.builder().sortOrder(DESC).sortColumn(SUBMISSION_DATE)
                .searchQuery(EMPTY_STRING).build();
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
        final var requestDTO = RevalidationRequestDTO.builder().sortOrder(DESC).sortColumn(SUBMISSION_DATE)
                .searchQuery(EMPTY_STRING).build();
        when(doctorsForDBService.getAllTraineeDoctorDetails(requestDTO)).thenReturn(gmcDoctorDTO);
        this.mockMvc.perform(get(DOCTORS_API_URL)
                .param(SORT_ORDER, "aa")
                .param(SORT_COLUMN, "date"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(gmcDoctorDTO)));
    }

    @Test
    public void shouldReturnUnderNoticeTraineeDoctorsInformation() throws Exception {
        final var gmcDoctorDTO = prepareGmcDoctor();
        final var requestDTO = RevalidationRequestDTO.builder()
                .sortOrder(ASC).sortColumn(SUBMISSION_DATE).underNotice(true).searchQuery(EMPTY_STRING).build();
        when(doctorsForDBService.getAllTraineeDoctorDetails(requestDTO)).thenReturn(gmcDoctorDTO);
        this.mockMvc.perform(get("/api/v1/doctors")
                .param(SORT_ORDER, ASC)
                .param(SORT_COLUMN, SUBMISSION_DATE)
                .param(UNDER_NOTICE, String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(gmcDoctorDTO)));
    }

    private TraineeDoctorDTO prepareGmcDoctor() {
        final var doctorsForDB = buildDoctorsForDBList();
        return TraineeDoctorDTO.builder()
                .traineeInfo(doctorsForDB)
                .countTotal(doctorsForDB.size())
                .countUnderNotice(1l)
                .build();
    }

    private List<TraineeInfoDTO> buildDoctorsForDBList() {
        final var doctor1 = TraineeInfoDTO.builder()
                .gmcReferenceNumber(gmcRef1)
                .doctorFirstName(firstName1)
                .doctorLastName(lastName1)
                .submissionDate(submissionDate1)
                .dateAdded(dateAdded1)
                .underNotice(underNotice1.value())
                .sanction(sanction1)
                .doctorStatus(doctorStatus1.value())
                .build();

        final var doctor2 = TraineeInfoDTO.builder()
                .gmcReferenceNumber(gmcRef2)
                .doctorFirstName(firstName2)
                .doctorLastName(lastName2)
                .submissionDate(submissionDate2)
                .dateAdded(dateAdded2)
                .underNotice(underNotice2.value())
                .sanction(sanction2)
                .doctorStatus(doctorStatus2.value())
                .build();
        return of(doctor1, doctor2);
    }

}
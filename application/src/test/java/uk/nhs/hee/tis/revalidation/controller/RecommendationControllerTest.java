package uk.nhs.hee.tis.revalidation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.hee.tis.revalidation.dto.TraineeInfoDTO;
import uk.nhs.hee.tis.revalidation.entity.RevalidationStatus;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.service.RecommendationService;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(RecommendationController.class)
public class RecommendationControllerTest {

    private static final String RECOMMENDATION_API_URL = "/api/recommendation/{gmcId}";
    private final Faker faker = new Faker();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RecommendationService service;

    private String gmcId = faker.number().digits(8);
    private String firstName = faker.name().firstName();
    private String lastName = faker.name().lastName();
    private LocalDate submissionDate = LocalDate.now();
    private LocalDate dateAdded = LocalDate.now();;
    private UnderNotice underNotice = faker.options().option(UnderNotice.class);
    private String sanction = faker.lorem().characters(2);
    private RevalidationStatus status =  faker.options().option(RevalidationStatus.class);
    private LocalDate cctDate = LocalDate.now();
    private String programmeName = faker.lorem().sentence(3);
    private String programmeMembershipType = faker.lorem().characters(10);
    private String currentGrade = faker.lorem().characters(4);

    @Test
    public void shouldReturnTraineeRecommendation() throws Exception {
        final TraineeInfoDTO traineeInfoDTO = prepareTraineeInforDTO();
        when(service.getTraineeInfo(gmcId)).thenReturn(traineeInfoDTO);
        this.mockMvc.perform(get(RECOMMENDATION_API_URL, gmcId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(traineeInfoDTO)));

    }

    private TraineeInfoDTO prepareTraineeInforDTO() {
        return TraineeInfoDTO.builder()
                .doctorFirstName(firstName)
                .doctorFirstName(lastName)
                .submissionDate(submissionDate)
                .dateAdded(dateAdded)
                .sanction(sanction)
                .underNotice(underNotice.value())
                .currentGrade(currentGrade)
                .programmeMembershipType(programmeMembershipType)
                .programmeName(programmeName)
                .cctDate(cctDate)
                .doctorStatus(status.value())
                .build();
    }

}
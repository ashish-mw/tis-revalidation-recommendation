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
import uk.nhs.hee.tis.revalidation.dto.RecommendationDTO;
import uk.nhs.hee.tis.revalidation.dto.RevalidationDTO;
import uk.nhs.hee.tis.revalidation.entity.*;
import uk.nhs.hee.tis.revalidation.service.RecommendationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private String deferralReason = faker.options().option(DeferralReason.class).name();
    private String deferralDate = "2018-03-15";
    private String deferralComments = faker.lorem().sentence(5);
    private String revalidationType = faker.options().option(RevalidationType.class).name();
    private String gmcOutcome = faker.options().option(RevalidationGmcOutcome.class).name();
    private LocalDateTime gmcSubmissionDate = LocalDateTime.now();
    private LocalDate actualSubmissionDate = LocalDate.now();
    private String admin = faker.name().fullName();

    @Test
    public void shouldReturnTraineeRecommendation() throws Exception {
        final var recommendationDTO = prepareRecommendationDTO();
        when(service.getTraineeInfo(gmcId)).thenReturn(recommendationDTO);
        this.mockMvc.perform(get(RECOMMENDATION_API_URL, gmcId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(recommendationDTO)));

    }

    private RecommendationDTO prepareRecommendationDTO() {
        return RecommendationDTO.builder()
                .fullName(firstName+" "+lastName)
                .gmcNumber(gmcId)
                .currentGrade(currentGrade)
                .programmeMembershipType(programmeMembershipType)
                .cctDate(cctDate)
                .revalidations(List.of(prepareRevalidationDTO()))
                .build();
    }

    private RevalidationDTO prepareRevalidationDTO() {
        return RevalidationDTO.builder()
                .deferralComment(deferralComments)
                .deferralDate(deferralDate)
                .deferralReason(deferralReason)
                .gmcOutcome(gmcOutcome)
                .revalidationType(revalidationType)
                .admin(admin)
                .gmcSubmissionDate(gmcSubmissionDate)
                .actualSubmissionDate(actualSubmissionDate)
                .build();
    }

}
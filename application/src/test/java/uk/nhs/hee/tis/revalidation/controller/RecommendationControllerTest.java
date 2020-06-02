package uk.nhs.hee.tis.revalidation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationDTO;
import uk.nhs.hee.tis.revalidation.dto.TraineeRecommendationRecordDTO;
import uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome;
import uk.nhs.hee.tis.revalidation.entity.RecommendationStatus;
import uk.nhs.hee.tis.revalidation.entity.RecommendationType;
import uk.nhs.hee.tis.revalidation.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.service.RecommendationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationType.*;

@RunWith(SpringRunner.class)
@WebMvcTest(RecommendationController.class)
public class RecommendationControllerTest {

    private static final String RECOMMENDATION_API_URL = "/api/recommendation";
    private static final String RECOMMENDATION_API_GMCID_PATH_VARIABLE = "{gmcId}";
    private static final String RECOMMENDATION_API_SUBMIT_PATH_VARIABLE = "/{gmcId}/submit/{recommendationId}";
    private final Faker faker = new Faker();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RecommendationService service;

    private String gmcId = faker.number().digits(7);
    private String recommendationId = faker.number().digits(8);
    private String firstName = faker.name().firstName();
    private String lastName = faker.name().lastName();
    private LocalDate submissionDate = LocalDate.now();
    private LocalDate dateAdded = LocalDate.now();
    ;
    private UnderNotice underNotice = faker.options().option(UnderNotice.class);
    private String sanction = faker.lorem().characters(2);
    private RecommendationStatus status = faker.options().option(RecommendationStatus.class);
    private LocalDate cctDate = LocalDate.now();
    private String programmeName = faker.lorem().sentence(3);
    private String programmeMembershipType = faker.lorem().characters(10);
    private String currentGrade = faker.lorem().characters(4);
    private String deferralReason = faker.lorem().sentence(2);
    private String deferralSubReason = faker.lorem().sentence(3);
    private LocalDate deferralDate = LocalDate.now();
    private String deferralComments = faker.lorem().sentence(5);
    private String recommendationType = faker.options().option(RecommendationType.class).name();
    private String gmcOutcome = faker.options().option(RecommendationGmcOutcome.class).name();
    private LocalDateTime gmcSubmissionDate = LocalDateTime.now();
    private LocalDate actualSubmissionDate = LocalDate.now();
    private String admin = faker.name().fullName();

    @Test
    public void shouldReturnTraineeRecommendation() throws Exception {
        final var recommendationDTO = prepareRecommendationDTO();
        when(service.getTraineeInfo(gmcId)).thenReturn(recommendationDTO);
        final var url = format("%s/%s", RECOMMENDATION_API_URL, RECOMMENDATION_API_GMCID_PATH_VARIABLE);
        this.mockMvc.perform(get(url, gmcId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(recommendationDTO)));

    }

    @Test
    public void shouldSaveRevalidateRecommendation() throws Exception {
        final var recordDTO = TraineeRecommendationRecordDTO.builder()
                .gmcNumber(gmcId)
                .recommendationType(REVALIDATE.name())
                .comments(List.of())
                .build();

        this.mockMvc.perform(post(RECOMMENDATION_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(recordDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldSaveNonEngagementRecommendation() throws Exception {
        final var recordDTO = TraineeRecommendationRecordDTO.builder()
                .gmcNumber(gmcId)
                .recommendationType(NON_ENGAGEMENT.name())
                .comments(List.of())
                .build();

        this.mockMvc.perform(post(RECOMMENDATION_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(recordDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldSaveDeferRecommendation() throws Exception {
        final var recordDTO = TraineeRecommendationRecordDTO.builder()
                .gmcNumber(gmcId)
                .recommendationType(DEFER.name())
                .deferralDate(deferralDate)
                .deferralReason(deferralReason)
                .deferralSubReason(deferralSubReason)
                .comments(List.of())
                .build();

        this.mockMvc.perform(post(RECOMMENDATION_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(recordDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldThroughExceptionWhenGmcIdOrRecommendationTypeMissingInRecommendationRequest() throws Exception {
        final var recordDTO = TraineeRecommendationRecordDTO.builder()
                .gmcNumber("")
                .recommendationType("")
                .comments(List.of())
                .build();

        final var expectedErrors = List.of("Gmc Number can't be empty or null", "Recommendation type can't be empty or null");
        this.mockMvc.perform(post(RECOMMENDATION_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(recordDTO)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json(mapper.writeValueAsString(expectedErrors)));
    }

    @Test
    public void shouldThroughExceptionWhenRecommendationIsDeferAndDateAndReasonAreMissingInRecommendationRequest() throws Exception {
        final var recordDTO = TraineeRecommendationRecordDTO.builder()
                .gmcNumber(gmcId)
                .recommendationType(DEFER.name())
                .comments(List.of())
                .build();

        final var expectedErrors = List.of("Deferral date can't be empty or null","Deferral Reason can't be empty or null");
        this.mockMvc.perform(post(RECOMMENDATION_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(recordDTO)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().json(mapper.writeValueAsString(expectedErrors)));
    }

    @Test
    public void shouldSubmitRecommendation() throws Exception {
        final var url = format("%s/%s", RECOMMENDATION_API_URL, RECOMMENDATION_API_SUBMIT_PATH_VARIABLE);
        this.mockMvc.perform(post(url, gmcId, recommendationId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private TraineeRecommendationDTO prepareRecommendationDTO() {
        return TraineeRecommendationDTO.builder()
                .fullName(firstName + " " + lastName)
                .gmcNumber(gmcId)
                .currentGrade(currentGrade)
                .programmeMembershipType(programmeMembershipType)
                .cctDate(cctDate)
                .revalidations(List.of(prepareRevalidationDTO()))
                .build();
    }

    private TraineeRecommendationRecordDTO prepareRevalidationDTO() {
        return TraineeRecommendationRecordDTO.builder()
                .deferralComment(deferralComments)
                .deferralDate(deferralDate)
                .deferralReason(deferralReason)
                .gmcOutcome(gmcOutcome)
                .recommendationType(recommendationType)
                .admin(admin)
                .gmcSubmissionDate(gmcSubmissionDate)
                .actualSubmissionDate(actualSubmissionDate)
                .build();
    }

}
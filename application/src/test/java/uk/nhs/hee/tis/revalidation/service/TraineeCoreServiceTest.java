package uk.nhs.hee.tis.revalidation.service;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import uk.nhs.hee.tis.revalidation.dto.TraineeCoreDTO;

import java.time.LocalDate;
import java.util.Map;

import static java.util.List.of;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TraineeCoreServiceTest {

    private static final String API_REVALIDATION = "/api/revalidation";
    private final Faker faker = new Faker();

    @InjectMocks
    private TraineeCoreService traineeCoreService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ResponseEntity responseEntity;

    private String gmcId1, gmcId2;
    private LocalDate cctDate1, cctDate2;
    private String programmeMembershipType1, programmeMembershipType2;
    private String programmeName1, programmeName2;
    private String currentGrade1, currentGrade2;
    private TraineeCoreDTO trainee1, trainee2;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(traineeCoreService, "tcsUrl", API_REVALIDATION);
        setupData();
    }

    @Test
    public void shouldFetchTraineeInformationFromTcs() {
        final var url = String.format("%s/%s", API_REVALIDATION, gmcId1);
        when(restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, TraineeCoreDTO>>() {
        })).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(Map.of(gmcId1, trainee1));
        final var traineeInformationFromCore = traineeCoreService.getTraineeInformationFromCore(of(gmcId1));
        assertThat(traineeInformationFromCore.size(), is(1));

        final var traineeCoreDTO = traineeInformationFromCore.get(gmcId1);
        assertThat(traineeCoreDTO, is(notNullValue()));
        assertThat(traineeCoreDTO.getGmcId(), is(gmcId1));
        assertThat(traineeCoreDTO.getCctDate(), is(cctDate1));
        assertThat(traineeCoreDTO.getCurrentGrade(), is(currentGrade1));
        assertThat(traineeCoreDTO.getProgrammeMembershipType(), is(programmeMembershipType1));
        assertThat(traineeCoreDTO.getProgrammeName(), is(programmeName1));
    }

    @Test
    public void shouldFetchMultipleTraineeInformationFromTcs() {
        final var url = String.format("%s/%s,%s", API_REVALIDATION, gmcId1, gmcId2);
        when(restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, TraineeCoreDTO>>() {
        })).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(Map.of(gmcId1, trainee1, gmcId2, trainee2));
        final var traineeInformationFromCore = traineeCoreService.getTraineeInformationFromCore(of(gmcId1, gmcId2));
        assertThat(traineeInformationFromCore.size(), is(2));

        var traineeCoreDTO = traineeInformationFromCore.get(gmcId1);
        assertThat(traineeCoreDTO, is(notNullValue()));
        assertThat(traineeCoreDTO.getGmcId(), is(gmcId1));
        assertThat(traineeCoreDTO.getCctDate(), is(cctDate1));
        assertThat(traineeCoreDTO.getCurrentGrade(), is(currentGrade1));
        assertThat(traineeCoreDTO.getProgrammeMembershipType(), is(programmeMembershipType1));
        assertThat(traineeCoreDTO.getProgrammeName(), is(programmeName1));

        traineeCoreDTO = traineeInformationFromCore.get(gmcId2);
        assertThat(traineeCoreDTO, is(notNullValue()));
        assertThat(traineeCoreDTO.getGmcId(), is(gmcId2));
        assertThat(traineeCoreDTO.getCctDate(), is(cctDate2));
        assertThat(traineeCoreDTO.getCurrentGrade(), is(currentGrade2));
        assertThat(traineeCoreDTO.getProgrammeMembershipType(), is(programmeMembershipType2));
        assertThat(traineeCoreDTO.getProgrammeName(), is(programmeName2));
    }

    @Test
    public void shouldReturnEmptyWhenNoRecordFound() {
        final var url = String.format("%s/%s", API_REVALIDATION, gmcId1);
        when(restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, TraineeCoreDTO>>() {
        })).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(Map.of());
        final var traineeInformationFromCore = traineeCoreService.getTraineeInformationFromCore(of(gmcId1));
        assertThat(traineeInformationFromCore.size(), is(0));
    }

    public void setupData() {
        gmcId1 = faker.number().digits(8);
        gmcId2 = faker.number().digits(8);

        cctDate1 = LocalDate.now();
        cctDate2 = LocalDate.now();

        programmeMembershipType1 = faker.lorem().characters(10);
        programmeMembershipType2 = faker.lorem().characters(10);

        programmeName1 = faker.lorem().sentence(3);
        programmeName2 = faker.lorem().sentence(3);

        currentGrade1 = faker.lorem().characters(5);
        currentGrade2 = faker.lorem().characters(5);

        trainee1 = new TraineeCoreDTO(gmcId1, cctDate1, programmeMembershipType1, programmeName1, currentGrade1);
        trainee2 = new TraineeCoreDTO(gmcId2, cctDate2, programmeMembershipType2, programmeName2, currentGrade2);
    }

}

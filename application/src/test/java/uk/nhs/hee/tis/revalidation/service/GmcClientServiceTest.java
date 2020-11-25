package uk.nhs.hee.tis.revalidation.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.INTERNAL_ERROR;
import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.INVALID_CREDENTIALS;
import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.INVALID_RECOMMENDATION;
import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.MISSING_OR_INVALID_GMC_REF_NUMBER;
import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.SUCCESS;
import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.YOUR_ACCOUNT_DOES_NOT_HAVE_ACCESS_TO_DB;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome.APPROVED;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome.UNDER_REVIEW;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.nhs.hee.tis.gmc.client.generated.CheckRecommendationStatus;
import uk.nhs.hee.tis.gmc.client.generated.CheckRecommendationStatusResponse;
import uk.nhs.hee.tis.gmc.client.generated.CheckRecommendationStatusResponseCT;

@ExtendWith(MockitoExtension.class)
public class GmcClientServiceTest {

  private final Faker faker = new Faker();

  @InjectMocks
  private GmcClientService gmcClientService;

  @Mock
  private WebServiceTemplate webServiceTemplate;

  @Mock
  private CheckRecommendationStatusResponse statusResponse;

  @Mock
  private CheckRecommendationStatusResponseCT statusResponseCT;

  private String gmcId = faker.number().digits(8);
  private String recommendationId = faker.number().digits(10); //internal recommendationId
  private String gmcRecommendationId = faker.number()
      .digits(10); //gmc Revalidation Id which we will receive when we submit recommendation.
  private String designatedBodyCode = faker.lorem().characters(8);
  private String url = faker.lorem().characters(10);
  private String username = faker.lorem().characters(10);
  private String password = faker.lorem().characters(10);

  @BeforeEach
  public void setup() {
    ReflectionTestUtils.setField(gmcClientService, "gmcConnectUrl", url);
    ReflectionTestUtils.setField(gmcClientService, "gmcUserName", username);
    ReflectionTestUtils.setField(gmcClientService, "gmcPassword", password);
  }


  @Test
  public void shouldReturnSuccessForCheckStatusOfRecommendation() {

    when(webServiceTemplate
        .marshalSendAndReceive(any(String.class), any(CheckRecommendationStatus.class)
            , any(SoapActionCallback.class))).thenReturn(statusResponse);
    when(statusResponse.getCheckRecommendationStatusResult()).thenReturn(statusResponseCT);
    when(statusResponseCT.getReturnCode()).thenReturn(SUCCESS.getCode());
    when(statusResponseCT.getStatus()).thenReturn(APPROVED.getOutcome());

    final var checkRecommendationStatusResponse = gmcClientService
        .checkRecommendationStatus(gmcId, gmcRecommendationId, recommendationId,
            designatedBodyCode);

    assertNotNull(checkRecommendationStatusResponse);
    assertThat(checkRecommendationStatusResponse, is(APPROVED));
  }

  @Test
  public void shouldReturnErrorForCheckStatusOfRecommendationWhenInvalidCredentials() {

    when(webServiceTemplate
        .marshalSendAndReceive(any(String.class), any(CheckRecommendationStatus.class)
            , any(SoapActionCallback.class))).thenReturn(statusResponse);
    when(statusResponse.getCheckRecommendationStatusResult()).thenReturn(statusResponseCT);
    when(statusResponseCT.getReturnCode()).thenReturn(INVALID_CREDENTIALS.getCode());

    final var checkRecommendationStatusResponse = gmcClientService.
        checkRecommendationStatus(gmcId, gmcRecommendationId, recommendationId, designatedBodyCode);

    assertNotNull(checkRecommendationStatusResponse);
    assertThat(checkRecommendationStatusResponse, is(UNDER_REVIEW));
  }

  @Test
  public void shouldReturnErrorForCheckStatusOfRecommendationWhenInvalidGmcNumber() {

    when(webServiceTemplate
        .marshalSendAndReceive(any(String.class), any(CheckRecommendationStatus.class)
            , any(SoapActionCallback.class))).thenReturn(statusResponse);

    when(statusResponse.getCheckRecommendationStatusResult()).thenReturn(statusResponseCT);
    when(statusResponseCT.getReturnCode()).thenReturn(MISSING_OR_INVALID_GMC_REF_NUMBER.getCode());

    final var checkRecommendationStatusResponse = gmcClientService.
        checkRecommendationStatus(gmcId, gmcRecommendationId, recommendationId, designatedBodyCode);

    assertNotNull(checkRecommendationStatusResponse);
    assertThat(checkRecommendationStatusResponse, is(UNDER_REVIEW));

  }

  @Test
  public void shouldReturnErrorForCheckStatusOfRecommendationWhenInternalError() {

    when(webServiceTemplate
        .marshalSendAndReceive(any(String.class), any(CheckRecommendationStatus.class)
            , any(SoapActionCallback.class))).thenReturn(statusResponse);

    when(statusResponse.getCheckRecommendationStatusResult()).thenReturn(statusResponseCT);
    when(statusResponseCT.getReturnCode()).thenReturn(INTERNAL_ERROR.getCode());

    final var checkRecommendationStatusResponse = gmcClientService.
        checkRecommendationStatus(gmcId, gmcRecommendationId, recommendationId, designatedBodyCode);

    assertNotNull(checkRecommendationStatusResponse);
    assertThat(checkRecommendationStatusResponse, is(UNDER_REVIEW));
  }

  @Test
  public void shouldReturnErrorForCheckStatusOfRecommendationWhenInvalidRecommendationId() {

    when(webServiceTemplate
        .marshalSendAndReceive(any(String.class), any(CheckRecommendationStatus.class)
            , any(SoapActionCallback.class))).thenReturn(statusResponse);

    when(statusResponse.getCheckRecommendationStatusResult()).thenReturn(statusResponseCT);
    when(statusResponseCT.getReturnCode()).thenReturn(INVALID_RECOMMENDATION.getCode());

    final var checkRecommendationStatusResponse = gmcClientService.
        checkRecommendationStatus(gmcId, gmcRecommendationId, recommendationId, designatedBodyCode);

    assertNotNull(checkRecommendationStatusResponse);
    assertThat(checkRecommendationStatusResponse, is(UNDER_REVIEW));
  }

  @Test
  public void shouldReturnErrorForCheckStatusOfRecommendationWhenNoDBAccess() {

    when(webServiceTemplate
        .marshalSendAndReceive(any(String.class), any(CheckRecommendationStatus.class)
            , any(SoapActionCallback.class))).thenReturn(statusResponse);

    when(statusResponse.getCheckRecommendationStatusResult()).thenReturn(statusResponseCT);
    when(statusResponseCT.getReturnCode())
        .thenReturn(YOUR_ACCOUNT_DOES_NOT_HAVE_ACCESS_TO_DB.getCode());

    final var checkRecommendationStatusResponse = gmcClientService.
        checkRecommendationStatus(gmcId, gmcRecommendationId, recommendationId, designatedBodyCode);

    assertNotNull(checkRecommendationStatusResponse);
    assertThat(checkRecommendationStatusResponse, is(UNDER_REVIEW));
  }
}
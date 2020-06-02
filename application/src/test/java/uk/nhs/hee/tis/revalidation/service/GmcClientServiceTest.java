package uk.nhs.hee.tis.revalidation.service;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.nhs.hee.tis.gmc.client.generated.CheckRecommendationStatus;
import uk.nhs.hee.tis.gmc.client.generated.CheckRecommendationStatusResponse;
import uk.nhs.hee.tis.gmc.client.generated.CheckRecommendationStatusResponseCT;
import uk.nhs.hee.tis.revalidation.entity.GmcResponseCode;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.*;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome.UNDER_REVIEW;

@RunWith(MockitoJUnitRunner.class)
public class GmcClientServiceTest {

    private final Faker faker = new Faker();

    @InjectMocks
    private GmcClientService gmcClientService;

    @Mock
    private WebServiceTemplate webServiceTemplate;

    private String gmcId = faker.number().digits(8);
    private String recommendationId = faker.number().digits(10); //internal recommendationId
    private String gmcRecommendationId = faker.number().digits(10); //gmc Revalidation Id which we will receive when we submit recommendation.
    private String designatedBodyCode = faker.lorem().characters(8);
    private String url = faker.lorem().characters(10);
    private String username = faker.lorem().characters(10);
    private String password = faker.lorem().characters(10);

    @Before
    public void setup() {
        ReflectionTestUtils.setField(gmcClientService, "gmcConnectUrl", url);
        ReflectionTestUtils.setField(gmcClientService, "gmcUserName", username);
        ReflectionTestUtils.setField(gmcClientService, "gmcPassword", password);
    }


    @Test
    public void shouldReturnSuccessForCheckStatusOfRecommendation() {

        when(webServiceTemplate.marshalSendAndReceive(any(String.class), any(CheckRecommendationStatus.class)
                , any(SoapActionCallback.class))).thenReturn(buildResponse(SUCCESS, UNDER_REVIEW.getOutcome(), recommendationId));

        final var checkRecommendationStatusResponse = gmcClientService.checkRecommendationStatus(gmcId, gmcRecommendationId, recommendationId, designatedBodyCode);

        final var checkRecommendationStatusResult = checkRecommendationStatusResponse.getCheckRecommendationStatusResult();
        assertNotNull(checkRecommendationStatusResult);
        assertThat(checkRecommendationStatusResult.getReturnCode(), is(SUCCESS.getCode()));
        assertThat(checkRecommendationStatusResult.getStatus(), is(UNDER_REVIEW.getOutcome()));
        assertThat(checkRecommendationStatusResult.getClientRequestID(), is(recommendationId));

    }

    @Test
    public void shouldReturnErrorForCheckStatusOfRecommendationWhenInvalidCredentials() {

        when(webServiceTemplate.marshalSendAndReceive(any(String.class), any(CheckRecommendationStatus.class)
                , any(SoapActionCallback.class))).thenReturn(buildResponse(INVALID_CREDENTIALS, null, null));

        final var checkRecommendationStatusResponse = gmcClientService.
                checkRecommendationStatus(gmcId, gmcRecommendationId, recommendationId, designatedBodyCode);

        final var checkRecommendationStatusResult = checkRecommendationStatusResponse.getCheckRecommendationStatusResult();
        assertNotNull(checkRecommendationStatusResult);
        assertThat(checkRecommendationStatusResult.getReturnCode(), is(INVALID_CREDENTIALS.getCode()));
    }

    @Test
    public void shouldReturnErrorForCheckStatusOfRecommendationWhenInvalidGmcNumber() {

        when(webServiceTemplate.marshalSendAndReceive(any(String.class), any(CheckRecommendationStatus.class)
                , any(SoapActionCallback.class))).thenReturn(buildResponse(MISSING_OR_INVALID_GMC_REF_NUMBER, null, null));

        final var checkRecommendationStatusResponse = gmcClientService.checkRecommendationStatus(gmcId, gmcRecommendationId, recommendationId, designatedBodyCode);

        final var checkRecommendationStatusResult = checkRecommendationStatusResponse.getCheckRecommendationStatusResult();
        assertNotNull(checkRecommendationStatusResult);
        assertThat(checkRecommendationStatusResult.getReturnCode(), is(MISSING_OR_INVALID_GMC_REF_NUMBER.getCode()));

    }

    @Test
    public void shouldReturnErrorForCheckStatusOfRecommendationWhenInternalError() {

        when(webServiceTemplate.marshalSendAndReceive(any(String.class), any(CheckRecommendationStatus.class)
                , any(SoapActionCallback.class))).thenReturn(buildResponse(INTERNAL_ERROR, null, null));

        final var checkRecommendationStatusResponse = gmcClientService.checkRecommendationStatus(gmcId, gmcRecommendationId, recommendationId, designatedBodyCode);

        final var checkRecommendationStatusResult = checkRecommendationStatusResponse.getCheckRecommendationStatusResult();
        assertNotNull(checkRecommendationStatusResult);
        assertThat(checkRecommendationStatusResult.getReturnCode(), is(INTERNAL_ERROR.getCode()));
    }

    @Test
    public void shouldReturnErrorForCheckStatusOfRecommendationWhenInvalidRecommendationId() {

        when(webServiceTemplate.marshalSendAndReceive(any(String.class), any(CheckRecommendationStatus.class)
                , any(SoapActionCallback.class))).thenReturn(buildResponse(INVALID_RECOMMENDATION, null, null));

        final var checkRecommendationStatusResponse = gmcClientService.checkRecommendationStatus(gmcId, gmcRecommendationId, recommendationId, designatedBodyCode);

        final var checkRecommendationStatusResult = checkRecommendationStatusResponse.getCheckRecommendationStatusResult();
        assertNotNull(checkRecommendationStatusResult);
        assertThat(checkRecommendationStatusResult.getReturnCode(), is(INVALID_RECOMMENDATION.getCode()));
    }

    @Test
    public void shouldReturnErrorForCheckStatusOfRecommendationWhenNoDBAccess() {

        when(webServiceTemplate.marshalSendAndReceive(any(String.class), any(CheckRecommendationStatus.class)
                , any(SoapActionCallback.class))).thenReturn(buildResponse(YOUR_ACCOUNT_DOES_NOT_HAVE_ACCESS_TO_DB, null, null));

        final var checkRecommendationStatusResponse = gmcClientService.checkRecommendationStatus(gmcId, gmcRecommendationId, recommendationId, designatedBodyCode);

        final var checkRecommendationStatusResult = checkRecommendationStatusResponse.getCheckRecommendationStatusResult();
        assertNotNull(checkRecommendationStatusResult);
        assertThat(checkRecommendationStatusResult.getReturnCode(), is(YOUR_ACCOUNT_DOES_NOT_HAVE_ACCESS_TO_DB.getCode()));
    }


    private CheckRecommendationStatusResponse buildResponse(final GmcResponseCode responseCode, final String status, final String clientRequestId) {
        final var response = new CheckRecommendationStatusResponse();
        final var checkRecommendationStatusResponseCT = new CheckRecommendationStatusResponseCT();
        if (SUCCESS.equals(responseCode)) {
            checkRecommendationStatusResponseCT.setReturnCode(responseCode.getCode());
            checkRecommendationStatusResponseCT.setStatus(status);
            checkRecommendationStatusResponseCT.setClientRequestID(clientRequestId);
        } else {
            checkRecommendationStatusResponseCT.setReturnCode(responseCode.getCode());
        }
        response.setCheckRecommendationStatusResult(checkRecommendationStatusResponseCT);
        return response;
    }

}
package uk.nhs.hee.tis.revalidation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import uk.nhs.hee.tis.gmc.client.generated.*;
import uk.nhs.hee.tis.revalidation.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.entity.Recommendation;
import uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome;

import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.SUCCESS;
import static uk.nhs.hee.tis.revalidation.entity.GmcResponseCode.fromCode;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationGmcOutcome.UNDER_REVIEW;
import static uk.nhs.hee.tis.revalidation.entity.RecommendationType.DEFER;
import static uk.nhs.hee.tis.revalidation.util.DateUtil.convertDateInGmcFormat;

@Slf4j
@Service
public class GmcClientService {

    private static final String INTERNAL_USER_ID = "InternalUserId";

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Value("${app.gmc.url}")
    private String gmcConnectUrl;

    @Value("${app.gmc.gmcUsername}")
    private String gmcUserName;

    @Value("${app.gmc.gmcPassword}")
    private String gmcPassword;

    public RecommendationGmcOutcome checkRecommendationStatus(final String gmcNumber,
                                                              final String gmcRecommendationId,
                                                              final String recommendationId,
                                                              final String designatedBody) {

        final var checkRecommendationStatus =
                buildCheckRecommendationStatusRequest(gmcNumber, gmcRecommendationId, recommendationId, designatedBody);
        log.info("GMC Connect Url {}", gmcConnectUrl);
        try {
            final var checkRecommendationStatusResponse = (CheckRecommendationStatusResponse) webServiceTemplate
                    .marshalSendAndReceive(gmcConnectUrl, checkRecommendationStatus,
                            new SoapActionCallback(gmcConnectUrl));

            final var checkRecommendationStatusResult = checkRecommendationStatusResponse.getCheckRecommendationStatusResult();
            final var gmdReturnCode = checkRecommendationStatusResult.getReturnCode();
            if (SUCCESS.getCode().equals(gmdReturnCode)) {
                final var status = checkRecommendationStatusResult.getStatus();
                return RecommendationGmcOutcome.fromString(status);
            } else {
                final var responseCode = fromCode(gmdReturnCode);
                log.error("Gmc recommendation check status request is failed for GmcId: {} and recommendationId: {} with Response: {}." +
                        " Recommendation will stay in Under Review state", gmcNumber, recommendationId, responseCode.getMessage());
            }
        } catch (Exception e) {
            log.error("Failed to check status with GMC", e);
        }

        return UNDER_REVIEW;
    }

    public TryRecommendationV2Response submitToGmc(final DoctorsForDB doctorForDB, final Recommendation recommendation) {
        final TryRecommendationV2 tryRecommendation = new TryRecommendationV2();
        final TryRecommendationV2Request request = new TryRecommendationV2Request();
        request.setDoctorSurname(doctorForDB.getDoctorLastName());
        request.setDoctorUID(doctorForDB.getGmcReferenceNumber());
        request.setRoSurname("SurName"); //TODO: find RO details
        request.setRoUID("0777777"); //TODO: find RO details
        request.setRoDesignatedBodyCode(doctorForDB.getDesignatedBodyCode()); //TODO: find RO details
        request.setRoPhoneNumber("07999999999"); //TODO: find RO details
        request.setRoRecommendation(recommendation.getRecommendationType().getCode());
        if (DEFER.equals(recommendation.getRecommendationType())) {
            request.setRoRecommendationReason(recommendation.getDeferralReason());
            request.setRoRecommendationSubreason(recommendation.getDeferralSubReason());
            request.setRequestedDeferralDate(convertDateInGmcFormat(recommendation.getDeferralDate()));
        } else {
            request.setRoRecommendationReason("");
            request.setRoRecommendationSubreason("");
            request.setRequestedDeferralDate(null);
        }
        request.setClientRequestID(recommendation.getId().toString());
        request.setInternalUser(INTERNAL_USER_ID); //TODO: find Internal User details
        request.setInternalUserEmailAddress("info@admin.com"); //TODO: find Internal User details
        tryRecommendation.setRecReq(request);
        tryRecommendation.setUsername(gmcUserName);
        tryRecommendation.setPassword(gmcPassword);

        log.info("GMC Connect Url {}", gmcConnectUrl);
        try {
            final var tryRecommendationV2Response = (TryRecommendationV2Response) webServiceTemplate
                    .marshalSendAndReceive(gmcConnectUrl, tryRecommendation,
                            new SoapActionCallback(gmcConnectUrl));
            return tryRecommendationV2Response;
        } catch (Exception e) {
            log.error("Failed to submit to GMC", e);
        }
        return new TryRecommendationV2Response();
    }

    private CheckRecommendationStatus buildCheckRecommendationStatusRequest(final String gmcNumber,
                                                                            final String gmcRecommendationId,
                                                                            final String recommendationId,
                                                                            final String designatedBody) {
        final var checkRecommendationStatus = new CheckRecommendationStatus();
        final var checkRecommendationStatusRequest = new CheckRecommendationStatusRequest();
        checkRecommendationStatusRequest.setGMCReferenceNumber(gmcNumber);
        checkRecommendationStatusRequest.setRecommendationId(gmcRecommendationId);
        checkRecommendationStatusRequest.setDesignatedBodyId(designatedBody);
        checkRecommendationStatusRequest.setClientRequestId(recommendationId);
        checkRecommendationStatusRequest.setInternalUserId(INTERNAL_USER_ID); //TODO: replace when we have login details
        checkRecommendationStatus.setRequest(checkRecommendationStatusRequest);
        checkRecommendationStatus.setUsername(gmcUserName);
        checkRecommendationStatus.setPassword(gmcPassword);
        return checkRecommendationStatus;
    }
}

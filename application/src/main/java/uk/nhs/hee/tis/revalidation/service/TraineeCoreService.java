package uk.nhs.hee.tis.revalidation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.nhs.hee.tis.revalidation.dto.TraineeCoreDTO;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpMethod.GET;

@Slf4j
@Service
public class TraineeCoreService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.reval.tcs.url}")
    private String tcsUrl;

    public Map<String, TraineeCoreDTO> getTraineeInformationFromCore(final List<String> gmcIds) {

        if (!gmcIds.isEmpty()) {
            final var gmcId = gmcIds.stream().collect(joining(","));
            final var requestUrl = format("%s/%s", tcsUrl, gmcId);
            Map<String, TraineeCoreDTO> traineeCoreDTOS = Map.of();
            try {
                traineeCoreDTOS = restTemplate
                        .exchange(requestUrl, GET, null,
                                new ParameterizedTypeReference<Map<String, TraineeCoreDTO>>() {
                                }).getBody();

            } catch (final HttpStatusCodeException exception) {
                int statusCode = exception.getStatusCode().value();
                log.error("Fail to connect to TCS service. Status code: {}", statusCode, exception);
            } catch (final Exception e) {
                log.error("Fail to connect to TCS service", e);
            }

            return traineeCoreDTOS;
        }

        return Map.of();
    }
}

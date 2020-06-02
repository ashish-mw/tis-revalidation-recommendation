package uk.nhs.hee.tis.revalidation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.nhs.hee.tis.revalidation.validator.TraineeRecommendationRecordDTOValidator;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@SpringBootApplication
public class RevalidationApplication {

    public static void main(String[] args) {
        SpringApplication.run(RevalidationApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        final var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public TraineeRecommendationRecordDTOValidator traineeRecommendationRecordDTOValidator() {
        return new TraineeRecommendationRecordDTOValidator();
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("uk.nhs.hee.tis.gmc.client", "uk.nhs.hee.tis.gmc.client.generated");
        return marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() {
        final WebServiceTemplate webServiceTemplate =  new WebServiceTemplate();
        webServiceTemplate.setMarshaller(marshaller());
        webServiceTemplate.setUnmarshaller(marshaller());
        return webServiceTemplate;
    }
}

package uk.nhs.hee.tis.revalidation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2).select()
        .apis(RequestHandlerSelectors
            .basePackage("uk.nhs.hee.tis.revalidation.controller"))
        .paths(PathSelectors.regex("/.*"))
        .build().apiInfo(apiEndPointsInfo());
  }

  private ApiInfo apiEndPointsInfo() {
    return new ApiInfoBuilder().title("TIS HEE Revalidation")
        .description("Revalidation REST API")
        .contact(new Contact("HEE", "https://www.hee.nhs.uk/", "info@hee.nhs.uk"))
        .license("")
        .licenseUrl("")
        .termsOfServiceUrl("")
        .version("1.0.0")
        .build();
  }
}

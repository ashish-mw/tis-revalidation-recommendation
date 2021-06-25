package uk.nhs.hee.tis.revalidation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebConfig {

  @Bean
  public CorsFilter addCorsMappings() {
    final var source = new UrlBasedCorsConfigurationSource();

    final var config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOriginPattern("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");

    source.registerCorsConfiguration("/api/**", config);
    source.registerCorsConfiguration("/swagger-ui.html", config);
    return new CorsFilter(source);
  }
}

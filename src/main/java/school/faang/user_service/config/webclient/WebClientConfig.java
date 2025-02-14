package school.faang.user_service.config.webclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import school.faang.user_service.properties.DiceBearProperties;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient diceBearWebClient(DiceBearProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}

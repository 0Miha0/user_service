package school.faang.user_service.service.dicebear;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import school.faang.user_service.exception.customexception.DiceBearException;
import school.faang.user_service.properties.DiceBearProperties;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiceBearService {

    private final WebClient webClient;
    private final DiceBearProperties diceBearProperties;

    @Qualifier("diceBearWebClient")
    public Optional<byte[]> getRandomAvatar(Long userId) {
        log.info("Requesting random avatar for user ID: {} with style: {}, format: {}",
                userId, diceBearProperties.getStyle(), diceBearProperties.getFormat());

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(
                                diceBearProperties.getVersion(),
                                diceBearProperties.getStyle(),
                                diceBearProperties.getFormat())
                        .queryParam("seed", userId)
                        .build())
                .retrieve()
                .bodyToMono(byte[].class)
                .onErrorMap(WebClientResponseException.class, e -> {
                    log.warn("Error retrieving avatar for user ID: {}. Message: {}", userId, e.getMessage());
                    return new DiceBearException(e.getMessage());
                })
                .onErrorMap(Exception.class, e -> {
                    log.warn("Unexpected error retrieving avatar for user ID: {}. Message: {}", userId, e.getMessage());
                    return new DiceBearException(e.getMessage());
                })
                .filter(avatarData -> avatarData != null && avatarData.length > 0)
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .block();
    }
}

package school.faang.user_service.filter.recommendationRequest;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public interface RecommendationRequestFilter {
    boolean isApplicable(RecommendationRequestFilterDto filterDto);

    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequest, RecommendationRequestFilterDto filterDto);
}

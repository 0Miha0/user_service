package school.faang.user_service.filter.recommendationRequest;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class RecommendationRequestRejectionReasonFilter implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filterDto) {
        return filterDto.getRejectionReason() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests, RecommendationRequestFilterDto filterDto) {
        return recommendationRequests.filter(request -> request.getRejectionReason().equals(filterDto.getRejectionReason()));
    }
}

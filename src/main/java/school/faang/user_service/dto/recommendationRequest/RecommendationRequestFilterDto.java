package school.faang.user_service.dto.recommendationRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequestFilterDto {

    private Long requesterId;

    private Long receiverId;

    private RequestStatus status;

    private LocalDateTime createdAfter;

    private String rejectionReason;
}

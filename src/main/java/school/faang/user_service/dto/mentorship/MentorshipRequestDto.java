package school.faang.user_service.dto.mentorship;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class MentorshipRequestDto {

    private Long id;
    private String description;
    private Long requesterId;
    private Long receiverId;
    private RequestStatus status;
    private String rejectionReason;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}

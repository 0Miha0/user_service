package school.faang.user_service.event_drive.redis.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileViewEvent {

    @NotNull
    private Long receiverId;

    @NotNull
    private Long actorId;

    @NotNull
    private LocalDateTime receivedAt;
}
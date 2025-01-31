package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalInvitationDto {

    private Long id;

    @NotNull
    private Long inviterId;

    @NotNull
    private Long invitedUserId;

    @NotNull
    private Long goalId;
}

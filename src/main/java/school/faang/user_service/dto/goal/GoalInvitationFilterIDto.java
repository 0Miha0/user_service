package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.RequestStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalInvitationFilterIDto {

    private RequestStatus status;
}

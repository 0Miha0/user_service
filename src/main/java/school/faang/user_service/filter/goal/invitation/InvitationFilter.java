package school.faang.user_service.filter.goal.invitation;

import school.faang.user_service.dto.goal.GoalInvitationFilterIDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public interface InvitationFilter {
    boolean isApplicable(GoalInvitationFilterIDto filter);

    Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, GoalInvitationFilterIDto filter);
}

package school.faang.user_service.filter.goal.invitation;

import school.faang.user_service.dto.goal.GoalInvitationFilterIDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

public class InvitationStatusFilter implements InvitationFilter {
    @Override
    public boolean isApplicable(GoalInvitationFilterIDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, GoalInvitationFilterIDto filter) {
        return invitations.filter(invitation -> invitation.getStatus() == filter.getStatus());
    }
}

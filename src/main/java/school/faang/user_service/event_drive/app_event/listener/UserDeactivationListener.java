package school.faang.user_service.event_drive.app_event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.event_drive.app_event.publisher.UserDeactivationEvent;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.user.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "listeners.userDeactivation", havingValue = "true", matchIfMissing = true)
public class UserDeactivationListener {

    private final UserService userService;
    private final GoalService goalService;
    private final MentorshipService mentorshipService;
    private final EventService eventService;

    @EventListener
    public void handleUserDeactivationEvent(UserDeactivationEvent event) {
        User user = userService.findById(event.getUserId());
        eventService.cancelingUserEvents(user.getOwnedEvents());
        goalService.deleteAllById(goalService.getIdsSetGoals(user.getSetGoals()));
        mentorshipService.deleteMentorFromMentee(user.getMentees());
        log.info("Event received: {}", event);
    }
}

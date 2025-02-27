package school.faang.user_service.event_drive.app_event.publisher;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserDeactivationEvent extends ApplicationEvent {
    private final Long userId;

    public UserDeactivationEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}

package school.faang.user_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.customexception.DataValidationException;

@Slf4j
@Component
public class EventValidator {

    public void validateEvent(EventDto event) {
        if (event == null) {
            throw new DataValidationException("Event cannot be null");
        }

        if (event.getTitle() == null || event.getTitle().isEmpty() || event.getTitle().isBlank()) {
            throw new DataValidationException("Event title cannot be empty");
        }

        if (event.getStartDate() == null) {
            throw new DataValidationException("Event start date cannot be null");
        }

        if (event.getOwnerId() == null) {
            throw new DataValidationException("Event user cannot be null");
        }
    }

    public void isTheUserTheAuthorOfTheEvent(Long existingEventOwnerId, Long eventOwnerId){
        if (!existingEventOwnerId.equals(eventOwnerId)) {
            throw new DataValidationException("User is not the owner of this event.");
        }
    }
}

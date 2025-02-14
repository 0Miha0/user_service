package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.customexception.DataValidationException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoalValidator {

    public void validateGoalStatus(GoalStatus goalStatus) {
        if (goalStatus == GoalStatus.COMPLETED) {
            log.info("Goal with id '{}' is already completed", goalStatus.name());
            throw new DataValidationException("Goal is already completed");
        }
    }
}

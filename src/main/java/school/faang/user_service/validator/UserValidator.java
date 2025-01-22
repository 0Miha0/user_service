package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {

    public void validateUserExistence(boolean isExist) {
        if (!isExist) {
            throw new EntityNotFoundException("User not exists");
        }
    }
}

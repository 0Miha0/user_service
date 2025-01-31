package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateUserExistence(boolean isExist) {
        if (!isExist) {
            throw new EntityNotFoundException("User not exists");
        }
    }

    public void isUserExists(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User with id: " + userId + " not found in DB"));
    }

    public void areUsersExist(Long firstUserId, Long secondUserId) {
        isUserExists(firstUserId);
        isUserExists(secondUserId);
    }
}

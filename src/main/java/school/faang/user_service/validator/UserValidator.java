package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.customexception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateUserExistence(boolean isExist) {
        log.info("Validating user existence: {}", isExist);
        if (!isExist) {
            log.warn("User with id '{}' does not exist");
            throw new DataValidationException("User not exists");
        }
    }

    public void isUserExists(Long userId) {
        log.info("Validating user with id: {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with id '{}' does not exist", userId);
                    return new DataValidationException("User with id: " + userId + " not found in DB");
                });
    }

    public void validateFileExistence(MultipartFile file, String fileType) {
        if(file == null || file.isEmpty() || !Objects.equals(file.getContentType(), fileType)) {
            log.warn("Invalid file format or file is empty");
            throw new DataValidationException("Invalid file format or file is empty");
        }
    }

    public void areUsersExist(Long firstUserId, Long secondUserId) {
        log.info("Validating users with ids: {}, {}", firstUserId, secondUserId);
        isUserExists(firstUserId);
        isUserExists(secondUserId);
    }
}

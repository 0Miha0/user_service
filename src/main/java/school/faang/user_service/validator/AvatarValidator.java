package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.customexception.MinioException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AvatarValidator {

    public void checkAvatarExists(UserProfilePic userProfilePic , Long userId) {
        if (userProfilePic != null && (userProfilePic.getFileId() != null || userProfilePic.getSmallFileId() != null)) {
            throw new MinioException(String.format("Avatar already exists for user ID: %d", userId));
        }
    }
}

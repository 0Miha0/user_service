package school.faang.user_service.service.avatar;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserProfilePicDto;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.customexception.AvatarProcessingException;
import school.faang.user_service.mapper.user.UserProfilePicMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.dicebear.DiceBearService;
import school.faang.user_service.service.minio.MinioService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.AvatarValidator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {

    private static final String AVATAR_FILE_FORMAT = "%d.png";
    private static final String SMALL_AVATAR_FILE_FORMAT = "%d_small.png";
    private static final int AVATAR_CONSTRAINT = 1080;
    private static final int SMALL_AVATAR_CONSTRAINT = 170;
    private static final String DEFAULT_AVATAR_CONTENT_TYPE = "image/png";

    private final MinioService minioService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final DiceBearService diceBearService;
    private final UserProfilePicMapper userProfilePicMapper;
    private final AvatarValidator avatarValidator;

    @Transactional(readOnly = true)
    public byte[] getUserAvatar(Long userId) {
        log.info("Start receiving avatar for user ID: {}", userId);
        UserProfilePic userProfilePic = userRepository.findUserProfilePicByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Avatar not found for user ID: %d", userId)));
        return minioService.downloadFile(userProfilePic.getFileId());
    }

    @Transactional
    public void deleteUserAvatar(Long userId) {
        log.info("Deleting avatar for user ID: {}", userId);
        UserProfilePic userProfilePic = userRepository.findUserProfilePicByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Error generating avatar using DiceBear service."));

        if (userProfilePic.getFileId() != null) {
            minioService.deleteFile(userProfilePic.getFileId());
        }
        if (userProfilePic.getSmallFileId() != null) {
            minioService.deleteFile(userProfilePic.getSmallFileId());
        }

        userRepository.deleteProfilePic(userId);
        log.info("Avatar successfully deleted for user ID: {}", userId);
    }

    @Transactional
    public UserProfilePicDto uploadUserAvatar(Long userId, MultipartFile file) {
        log.info("Start uploading avatar for user ID: {}", userId);
        avatarValidator.checkAvatarExists(userService.findById(userId).getUserProfilePic(), userId);

        byte[] avatarData;
        String contentType;

        if (file == null) {
            log.info("No file provided for user ID: {}. Generating random avatar.", userId);
            avatarData = getRandomDiceBearAvatar(userId);
            contentType = DEFAULT_AVATAR_CONTENT_TYPE;
        } else {
            try {
                avatarData = file.getBytes();
                contentType = file.getContentType();
                log.info("Uploaded avatar received for user ID: {}. File size: {} bytes, Content type: {}", userId, avatarData.length, contentType);
            } catch (IOException e) {
                log.warn("Failed to read the uploaded file for user ID: {}", userId, e);
                throw new RuntimeException("Failed to read the uploaded file", e);
            }
        }

        return userProfilePicMapper.toDto(processAndSaveAvatar(userId, avatarData, contentType));
    }

    private UserProfilePic processAndSaveAvatar(Long userId, byte[] avatarData, String contentType) {
        try {
            byte[] fileBytes = resizeImage(avatarData, AVATAR_CONSTRAINT);
            byte[] smallFileBytes = resizeImage(avatarData, SMALL_AVATAR_CONSTRAINT);

            String fileName = String.format(AVATAR_FILE_FORMAT, userId);
            String smallFileName = String.format(SMALL_AVATAR_FILE_FORMAT, userId);

            minioService.uploadFile(userId, fileName, fileBytes, contentType);
            minioService.uploadFile(userId, smallFileName, smallFileBytes, contentType);
            userRepository.updateProfilePic(userId, fileName, smallFileName);

            log.info("Avatar successfully uploaded for user ID: {}", userId);
            return new UserProfilePic(fileName, smallFileName);
        } catch (IOException e) {
            log.warn("Error resizing and saving avatar for user ID: {}", userId, e);
            throw new RuntimeException("Error processing avatar", e);
        }
    }

    public byte[] resizeImage(byte[] imageData, int size) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Thumbnails.of(inputStream)
                    .size(size, size)
                    .outputFormat("png")
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        }
    }

    public byte[] getRandomDiceBearAvatar(Long userId) {
        log.info("Requesting random avatar for user ID: {}", userId);
        return diceBearService.getRandomAvatar(userId)
                .orElseThrow(() -> new AvatarProcessingException("Failed to generate avatar"));
    }
}

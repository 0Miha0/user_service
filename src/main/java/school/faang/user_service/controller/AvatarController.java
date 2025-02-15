package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserProfilePicDto;
import school.faang.user_service.service.avatar.AvatarService;

@Tag(name = "Avatar")
@RestController
@RequestMapping("/avatar")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

    @Operation(summary = "Upload user avatar")
    @PostMapping("/{userId}/avatar")
    public ResponseEntity<UserProfilePicDto> uploadUserAvatar(@PathVariable Long userId,
                                                              @RequestParam(value = "file", required = false) MultipartFile file) {
        return ResponseEntity.ok(avatarService.uploadUserAvatar(userId, file));
    }

    @Operation(summary = "Get user avatar")
    @GetMapping("/{userId}/avatar")
    public ResponseEntity<Resource> getUserAvatar(@PathVariable Long userId) {
        byte[] avatarData = avatarService.getUserAvatar(userId);
        ByteArrayResource resource = new ByteArrayResource(avatarData);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .contentLength(avatarData.length)
                .body(resource);
    }

    @Operation(summary = "Delete user avatar")
    @DeleteMapping("/{userId}/avatar")
    public ResponseEntity<Void> deleteUserAvatar(@PathVariable Long userId) {
        avatarService.deleteUserAvatar(userId);
        return ResponseEntity.noContent().build();
    }
}


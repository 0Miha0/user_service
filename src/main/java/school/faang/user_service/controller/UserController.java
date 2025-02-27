package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.event_drive.redis.event.ProfileViewEvent;
import school.faang.user_service.event_drive.redis.publisher.ProfileViewEventPublisher;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "API for managing user", description = "API endpoints for managing user")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProfileViewEventPublisher profileViewEventPublisher;

    @Operation(summary = "Create a new user")
    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivationUser(@PathVariable Long userId) {
        userService.deactivationUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all not premium users")
    @PostMapping("/not-premium")
    public ResponseEntity<List<UserDto>> getNotPremiumUsers(@RequestBody UserFilterDto filters) {
        return ResponseEntity.ok(userService.getNotPremiumUsers(filters));
    }

    @Operation(summary = "Get all premium users")
    @PostMapping("/premium")
    public ResponseEntity<List<UserDto>> getPremiumUsers(@RequestBody UserFilterDto filters) {
        return ResponseEntity.ok(userService.getPremiumUsers(filters));
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Operation(summary = "Get users by ids")
    @GetMapping("/ids")
    public ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(userService.getUsersByIds(ids));
    }

    @Operation(summary = "Save user")
    @PostMapping
    public ResponseEntity<UserDto> saveUser(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.saveUser(userDto));
    }

    @Operation(summary = "Upload users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file format"),
            @ApiResponse(responseCode = "500", description = "Error occurred while uploading users")
    })
    @PostMapping("/upload")
    public ResponseEntity<List<UserDto>> uploadUsers(@RequestParam("csvFile") @NotNull MultipartFile csvFile) {
        return ResponseEntity.ok(userService.uploadUsers(csvFile));
    }

    @Operation(summary = "View profile")
    @GetMapping("/{profileUserId}/view")
    public ResponseEntity<Void> viewProfile(@PathVariable @NotNull Long profileUserId,
                                              @RequestHeader("x-user-id") Long userId
                                              ) {
        profileViewEventPublisher.publish(
                ProfileViewEvent.builder()
                        .receiverId(profileUserId)
                        .actorId(userId)
                        .receivedAt(LocalDateTime.now())
                        .build()
        );
        return ResponseEntity.ok().build();
    }
}

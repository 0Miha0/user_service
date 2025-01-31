package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Tag(name = "API for managing user", description = "API endpoints for managing user")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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

}

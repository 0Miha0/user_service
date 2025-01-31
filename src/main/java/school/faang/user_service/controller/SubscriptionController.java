package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.user_subscription.UserSubscriptionDto;
import school.faang.user_service.service.subscription.SubscriptionService;

import java.util.List;

@Tag(name = "API for managing user subscriptions", description = "Operations related to user subscriptions")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "Follow user")
    @PostMapping("/follow")
    public ResponseEntity<Void> followUser(@RequestParam Long followerId, @RequestParam Long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Unfollow user")
    @PostMapping("/unfollow")
    public ResponseEntity<Void> unfollowUser(@RequestParam Long followerId, @RequestParam Long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Check if follower is following followee")
    @GetMapping("/{followeeId}")
    public ResponseEntity<List<UserSubscriptionDto>> getFollowers(@PathVariable Long followeeId,
                                                            @RequestParam UserFilterDto filter) {
        subscriptionService.getFollowers(followeeId, filter);
        return ResponseEntity.ok(subscriptionService.getFollowers(followeeId, filter));
    }

    @Operation(summary = "Get followers count")
    @GetMapping("/count/{followeeId}")
    public ResponseEntity<Integer> getFollowersCount(@PathVariable Long followeeId) {
        return ResponseEntity.ok(subscriptionService.getFollowersCount(followeeId));
    }

    @Operation(summary = "Get following count")
    @GetMapping("/followings/{followeeId}")
    public ResponseEntity<List<UserSubscriptionDto>> getFollowing(@PathVariable Long followeeId,
                                                  @RequestParam UserFilterDto filter) {
        return ResponseEntity.ok(subscriptionService.getFollowing(followeeId, filter));
    }

    @Operation(summary = "Check if follower is following followee")
    @GetMapping("/followings/count/{followeeId}")
    public ResponseEntity<Integer> getFollowingCount(@PathVariable Long followeeId) {
        return ResponseEntity.ok(subscriptionService.getFollowingCount(followeeId));
    }
}

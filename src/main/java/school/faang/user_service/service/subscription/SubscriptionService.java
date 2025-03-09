package school.faang.user_service.service.subscription;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.user_subscription.UserSubscriptionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.event_drive.redis.event.FollowerEvent;
import school.faang.user_service.event_drive.redis.publisher.FollowerEventPublisher;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserSubscriptionMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionValidator;
import school.faang.user_service.validator.UserValidator;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserSubscriptionMapper userSubscriptionMapper;
    private final List<UserFilter> userFilters;
    private final SubscriptionValidator subscriptionValidator;
    private final UserValidator userValidator;
    private final FollowerEventPublisher followerEventPublisher;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        userValidator.areUsersExist(followerId, followeeId);
        subscriptionValidator.isFollowingExistsValidate(followerId, followeeId);

        subscriptionRepository.followUser(followerId, followeeId);
        log.info("User with id: {} follow user with id: {}", followerId, followeeId);

        followerEventPublisher.publish(
                FollowerEvent.builder()
                        .id(followeeId)
                        .build()
        );
    }

    @Transactional
    public void unfollowUser(Long followerId, Long followeeId) {
        userValidator.areUsersExist(followerId, followeeId);
        subscriptionValidator.isFollowingNotExistsValidate(followerId, followeeId);

        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("User with id: {} unfollow user with id: {}", followerId, followeeId);
    }

    public List<UserSubscriptionDto> getFollowers(Long followeeId, UserFilterDto filter) {
        userValidator.isUserExists(followeeId);

        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);

        followers = userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filter))
                .reduce(followers,
                        (users, userFilter) -> userFilter.apply(users, filter),
                        (a, b) -> b);

        List<User> filteredFollowers = followers.toList();
        log.info("Getting filtered followers for user with id {}", followeeId);
        return userSubscriptionMapper.toDtoList(filteredFollowers);
    }

    public int getFollowersCount(Long followeeId) {
        userValidator.isUserExists(followeeId);

        log.info("Getting followers count for user with id {}", followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserSubscriptionDto> getFollowing(Long followerId, UserFilterDto filter) {
        userValidator.isUserExists(followerId);

        Stream<User> followings = subscriptionRepository.findByFollowerId(followerId);

        followings = userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filter))
                .reduce(followings,
                        (users, userFilter) -> userFilter.apply(users, filter),
                        (a, b) -> b);

        List<User> filteredFollowers = followings.toList();
        log.info("Getting filtered followings for user with id {}", followerId);
        return userSubscriptionMapper.toDtoList(filteredFollowers);
    }

    public int getFollowingCount(Long followeeId) {
        userValidator.isUserExists(followeeId);

        log.info("Getting followings count for user with id: {}", followeeId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followeeId);
    }
}

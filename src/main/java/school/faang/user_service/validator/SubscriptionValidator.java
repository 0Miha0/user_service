package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.customexception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {

    private final SubscriptionRepository subscriptionRepository;

    public void isFollowingExistsValidate(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("You already follow this user");
        }
    }

    public void isFollowingNotExistsValidate(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("You don't follow this user");
        }
    }
}

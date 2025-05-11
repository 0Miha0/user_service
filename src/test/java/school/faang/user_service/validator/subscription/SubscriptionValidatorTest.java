package school.faang.user_service.validator.subscription;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionValidatorTest {

    @Mock
    SubscriptionRepository subscriptionRepository;

    @InjectMocks
    SubscriptionValidator subscriptionValidation;

    private long followerId;
    private long followeeId;

    @Test
    public void isFollowingExistsTest() {
        followerId = 1L;
        followeeId = 2L;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(true);

        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionValidation.isFollowingExistsValidate(followerId, followeeId));
    }

    @Test
    public void isFollowingNotExistsTest() {
        followerId = 1L;
        followeeId = 2L;
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId))
                .thenReturn(false);


        Assertions.assertThrows(DataValidationException.class,
                () -> subscriptionValidation.isFollowingNotExistsValidate(followerId, followeeId));
    }
}

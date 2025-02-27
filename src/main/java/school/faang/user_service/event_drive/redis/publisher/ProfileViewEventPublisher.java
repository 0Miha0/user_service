package school.faang.user_service.event_drive.redis.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event_drive.redis.event.ProfileViewEvent;

@Component
@RequiredArgsConstructor
public class ProfileViewEventPublisher implements EventPublisher<ProfileViewEvent>{

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.profile-view}")
    private String channel;

    @Override
    public void publish(ProfileViewEvent event) {
        redisTemplate.convertAndSend(channel, event);
    }
}

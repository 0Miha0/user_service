package school.faang.user_service.event_drive.redis.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event_drive.redis.event.FollowerEvent;

@Component
@RequiredArgsConstructor
public class FollowerEventPublisher implements EventPublisher<FollowerEvent>  {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.follower}")
    private String channel;

    @Override
    public void publish(FollowerEvent event) {
        redisTemplate.convertAndSend(channel, event);
    }
}

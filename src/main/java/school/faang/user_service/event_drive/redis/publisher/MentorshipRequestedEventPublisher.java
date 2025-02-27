package school.faang.user_service.event_drive.redis.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event_drive.redis.event.MentorshipRequestedEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class MentorshipRequestedEventPublisher implements EventPublisher<MentorshipRequestedEvent> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.mentorship-requested}")
    private String channel;

    public void publish(MentorshipRequestedEvent event) {
        redisTemplate.convertAndSend(channel, event);
    }
}

package school.faang.user_service.event_drive.redis.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event_drive.redis.event.CompletedTaskEvent;

@Component
@RequiredArgsConstructor
public class CompletedTaskEventPublisher implements EventPublisher<CompletedTaskEvent>{

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.completed-task}")
    private String completedTaskChannel;

    @Override
    public void publish(CompletedTaskEvent event) {
        redisTemplate.convertAndSend(completedTaskChannel, event);
    }
}

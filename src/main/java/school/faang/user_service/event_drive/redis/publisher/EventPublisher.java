package school.faang.user_service.event_drive.redis.publisher;

public interface EventPublisher<T> {

    void publish(T event);
}

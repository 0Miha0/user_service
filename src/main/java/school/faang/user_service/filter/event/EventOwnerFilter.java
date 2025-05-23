package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventOwnerFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getOwnerIdPattern() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        return events.filter(event -> event.getOwner().getId().equals(filter.getOwnerIdPattern()));
    }
}
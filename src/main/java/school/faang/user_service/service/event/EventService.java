package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.EventValidator;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final UserService userService;
    private final SkillService skillService;
    private final List<EventFilter> eventFilterDto;

    public EventDto createEvent(EventDto dto) {
        log.info("The beginning creation event with name: {}", dto.getTitle());
        eventValidator.validateEvent(dto);
        areThereAnySkillsEventRelated(dto);
        Event event = eventMapper.toEntity(dto);
        save(event);
        log.info("Event with name: {} successfully created", dto.getTitle());
        return eventMapper.toDto(event);
    }

    public EventDto getEvent(Long id) {
        log.info("The beginning get event with id: {}", id);
        Event event = findById(id);
        log.info("Event with id: {} successfully received", id);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        log.info("The beginning get events by filter");
        Stream<Event> eventStream = findAll().stream();

        eventStream = eventFilterDto.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filters))
                .reduce(eventStream,
                        (currentStream, eventFilter) -> eventFilter.apply(currentStream, filters),
                        (s1, s2) -> s2);

        List<Event> filteredEvents = eventStream.toList();

        log.info("On the specified filters found: {} events", filteredEvents.size());
        return eventMapper.toDtoList(filteredEvents);
    }

    public void deleteEvent(Long id) {
        log.info("The beginning delete event with id: {}", id);
        delete(id);
        log.info("Event with id: {} successfully deleted", id);
    }

    public EventDto updateEvent(EventDto dto) {
        log.info("The beginning update event with name: {}", dto.getTitle());
        Event existingEvent = findById(dto.getId());
        eventValidator.validateEvent(dto);
        eventValidator.isTheUserTheAuthorOfTheEvent(existingEvent.getOwner().getId(), dto.getOwnerId());
        areThereAnySkillsEventRelated(dto);
        Event updatedEvent = eventMapper.toEntity(dto);
        save(updatedEvent);
        log.info("Event with name: {} successfully updated", dto.getTitle());
        return eventMapper.toDto(updatedEvent);
    }

    public List<EventDto> getParticipatedEvents(Long id) {
        log.info("The beginning get particiapted event with id: {}", id);
        User user = userService.findById(id);
        log.info("Event with id: {} successfully get particiapted", id);
        return eventMapper.toDtoList(user.getParticipatedEvents());
    }

    public void deleteAllByIds(List<Long> id) {
        log.info("Delete all event");
        eventRepository.deleteAllById(id);
        log.info("All event deleted:");
    }

    public void delete(Long id) {
        log.info("Delete event with id: {}", id);
        eventRepository.deleteById(id);
        log.info("Event with id: {} deleted:", id);
    }

    public void save(Event event) {
        log.info("Save event with id: {},and title: {}", event.getId(), event.getTitle());
        eventRepository.save(event);
        log.info("Event with id: {},and title: {} saved:", event.getId(), event.getTitle());
    }

    public void saveAll(List<Event> event) {
        log.info("Save all events");
        eventRepository.saveAll(event);
        log.info("Event saved");
    }

    public List<Event> findAll() {
        log.info("The beginning get all events");
        return eventRepository.findAll();
    }

    public Event findById(Long id) {
        log.info("The beginning get event with id: {}", id);
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with ID " + id + " not found"));
    }

    private List<Long> getAllEventIds(List<Event> events) {
        log.info("The beginning get all event ids");
        return events.stream()
                .map(Event::getId)
                .toList();
    }

    public void cancelingUserEvents(List<Event> event) {
        log.info("The beginning canceled events");
        event.forEach(e -> e.setStatus(EventStatus.CANCELED));
        saveAll(event);
        deleteAllByIds(getAllEventIds(event));
        log.info("Events successfully canceled");
    }

    private void areThereAnySkillsEventRelated(EventDto dto) {
        log.info("The beginning checking related skills");
        List<Long> userSkillIds = skillService.getUserSkills(dto.getOwnerId())
                .stream()
                .map(SkillDto::getId)
                .toList();

        boolean allSkillsMatch = dto.getRelatedSkills().stream()
                .map(SkillDto::getId)
                .allMatch(userSkillIds::contains);

        if (!allSkillsMatch) {
            throw new IllegalArgumentException(
                    "User with ID " + dto.getOwnerId() + " does not have all the required skills for this event"
            );
        }
        log.info("All related skills are present");
    }
}

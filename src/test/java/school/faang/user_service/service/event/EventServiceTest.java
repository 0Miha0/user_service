package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.EventValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private UserService userService;

    @Spy
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Mock
    private EventValidator eventValidator;

    @InjectMocks
    private EventService eventService;

    private SkillDto skillDto1;
    private SkillDto skillDto2;
    private Skill skill1;
    private Skill skill2;
    private Event event;
    private EventDto eventDto;
    private Long userId;
    private Long eventId;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        eventId = 1L;

        skillDto1 = SkillDto.builder()
                .id(1L)
                .title("Skill")
                .build();
        skillDto2 = SkillDto.builder()
                .id(2L)
                .title("Skill 2")
                .build();

        skill1 = Skill.builder()
                .id(1L)
                .title("Skill")
                .build();
        skill2 = Skill.builder()
                .id(2L)
                .title("Skill 2")
                .build();

        eventDto = EventDto.builder()
                .id(1L)
                .title("Title")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .ownerId(1L)
                .description("Description")
                .relatedSkills(List.of(skillDto1, skillDto2))
                .type(EventType.WEBINAR)
                .status(EventStatus.IN_PROGRESS)
                .location("Location")
                .maxAttendees(100)
                .build();

        event = Event.builder()
                .id(1L)
                .title("Title")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .owner(userService.findById(1L))
                .description("Description")
                .relatedSkills(List.of(skill1, skill2))
                .type(EventType.WEBINAR)
                .status(EventStatus.IN_PROGRESS)
                .location("Location")
                .maxAttendees(100)
                .build();

    }

    @Test
    public void successfullyCreateEventTest() {
        when(skillService.getUserSkills(userId))
                .thenReturn(List.of(skillDto1, skillDto2));

        EventDto createdEventDto = eventService.createEvent(eventDto);

        assertEquals(eventDto.getId(), createdEventDto.getId());
        assertEquals(eventDto.getTitle(), createdEventDto.getTitle());
    }

    @Test
    public void checkForSkillMismatchTest() {
        when(skillService.getUserSkills(userId))
                .thenReturn(List.of(skillDto1));

        assertThrows(IllegalArgumentException.class,
                () -> eventService.createEvent(eventDto));
    }

    @Test
    public void successfullyGetEvent() {
        when(eventRepository.findById(eventId))
                .thenReturn(Optional.ofNullable(event));

        EventDto result = eventService.getEvent(eventId);

        verify(eventRepository).findById(eventId);
        assertEquals(event.getId(), result.getId());
        assertEquals(event.getTitle(), result.getTitle());
    }

//    @Test
//    public void successfullyGetEventsByFilterTest() {
//        Event event1 = Event.builder()
//                .title("Title")
//                .build();
//
//        Event event2 = Event.builder()
//                .title("Title")
//                .build();
//
//        Event event3 = Event.builder()
//                .title("Title2")
//                .build();
//
//        EventFilterDto eventFilterDto = EventFilterDto.builder()
//                .titlePattern("Title")
//                .build();
//
//        List<Event> events = List.of(event1, event2, event3);
//
//        when(eventRepository.findAll())
//                .thenReturn(events);
//
//        List<EventDto> result = eventService.getEventsByFilter(eventFilterDto);
//
//        verify(eventRepository).findAll();
//        assertEquals(2, result.size());
}































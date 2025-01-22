package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Tag(name = "API for managing events", description = "API endpoints for managing events")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Create event")
    @PostMapping
    public ResponseEntity<EventDto> createEvent(@Valid @RequestBody EventDto dto) {
        EventDto eventDto = eventService.createEvent(dto);
        return ResponseEntity.ok(eventDto);
    }

    @Operation(summary = "Get event")
    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long id) {
        EventDto eventDto = eventService.getEvent(id);
        return ResponseEntity.ok(eventDto);
    }

    @Operation(summary = "Get event with filter")
    @PostMapping("/filter")
    public ResponseEntity<List<EventDto>> getEventsByFilter(@RequestBody EventFilterDto dto) {
        List<EventDto> eventDto = eventService.getEventsByFilter(dto);
        return ResponseEntity.ok(eventDto);
    }

    @Operation(summary = "Delete event")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update event"  )
    @PatchMapping
    public ResponseEntity<EventDto> updateEvent(@Valid @RequestBody EventDto dto) {
        EventDto eventDto = eventService.updateEvent(dto);
        return ResponseEntity.ok(eventDto);
    }

    @Operation(summary = "Get participated events"  )
    @GetMapping("/participated/{userId}")
    public ResponseEntity<List<EventDto>> getParticipatedEvents(@PathVariable Long userId) {
        List<EventDto> eventDtos = eventService.getParticipatedEvents(userId);
        return ResponseEntity.ok(eventDtos);
    }
}

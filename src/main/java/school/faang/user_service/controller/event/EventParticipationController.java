package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserParticipationDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

@RestController
@RequestMapping("/events-participation")
@RequiredArgsConstructor
public class EventParticipationController {
    private final EventParticipationService service;

    @Operation(summary = "Register a new participant to an event")
    @PostMapping("/{eventId}/register")
    public ResponseEntity<Void> registerParticipant(@PathVariable Long eventId,
                                                    @RequestParam Long userId) {
        service.registerParticipant(eventId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Unregister a participant from an event")
    @PostMapping("/{eventId}/unregister")
    public ResponseEntity<Void> unregisterParticipant(@PathVariable Long eventId,
                                                      @RequestParam Long userId) {
        service.unregisterParticipant(eventId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get participants of an event")
    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<UserParticipationDto>> getParticipant(@PathVariable Long eventId) {
        return ResponseEntity.ok(service.getParticipant(eventId));
    }

    @Operation(summary = "Get total participants of an event")
    @GetMapping("/{eventId}/participants/count")
    public ResponseEntity<Integer> getParticipantsCount(@PathVariable Long eventId) {
        return ResponseEntity.ok(service.getParticipantsCount(eventId));
    }

}

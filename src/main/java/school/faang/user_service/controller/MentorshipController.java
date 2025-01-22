package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipUserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

@Tag(name = "API for managing mentorship", description = "API endpoints for managing mentorship")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @Operation(summary = "Get all mentee by user id")
    @GetMapping("/mentee/{userId}")
    public ResponseEntity<MentorshipUserDto> getMentee(@PathVariable Long userId) {
        return ResponseEntity.ok(mentorshipService.getMentee(userId));
    }

    @Operation(summary = "Get all mentors by user id")
    @GetMapping("/mentor/{userId}")
    public ResponseEntity<MentorshipUserDto> getMentor(@PathVariable Long userId) {
        return ResponseEntity.ok(mentorshipService.getMentor(userId));
    }

    @Operation(summary = "Delete mentee")
    @GetMapping("/mentee/{menteeId}/mentor/{mentorId}")
    public ResponseEntity<Void> deleteMentee(@PathVariable Long menteeId,
                                             @PathVariable Long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete mentor")
    @GetMapping("/mentor/{mentorId}/mentee/{menteeId}")
    public ResponseEntity<Void> deleteMentor(@PathVariable Long mentorId,
                                             @PathVariable Long menteeId) {
        mentorshipService.deleteMentor(mentorId, menteeId);
        return ResponseEntity.noContent().build();
    }
}

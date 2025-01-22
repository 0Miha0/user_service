package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterIDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@Tag(name = " API for managing goal invitations", description = "Operations for managing goal invitations")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/goal-invitations")
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @Operation(summary = "Send goal invitation")
    @PostMapping("/send")
    public ResponseEntity<Void> sendInvitationGoal(@RequestBody @Valid GoalInvitationDto goalInvitationDto) {
        goalInvitationService.sendInvitationGoal(goalInvitationDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Accept goal invitation")
    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptInvitationGoal(@PathVariable Long id) {
        goalInvitationService.acceptInvitationGoal(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reject goal invitation")
    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectGoalInvitation(@PathVariable Long id) {
        goalInvitationService.rejectGoalInvitation(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "View all invitation with filters")
    @PostMapping("/filter")
    public ResponseEntity<List<GoalInvitationDto>> viewAllInvitations(@RequestBody @Valid GoalInvitationFilterIDto goalInvitationFilterDto) {
        return ResponseEntity.ok(goalInvitationService.viewAllInvitations(goalInvitationFilterDto));
    }
}

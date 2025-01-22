package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Tag(name = "API for managing goals", description = "API endpoints for managing goals")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/goal")
public class GoalController {

    private final GoalService goalService;

    @Operation(summary = "Create goal")
    @PostMapping("/{userId}")
    public ResponseEntity<Void> createGoal(@PathVariable Long userId,
                                           @Valid @RequestBody GoalDto dto) {
        goalService.createGoal(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Update goal")
    @PutMapping("/{goalId}")
    public ResponseEntity<Void> updateGoal(@PathVariable Long goalId,
                                           @Valid @RequestBody GoalDto goalDto) {
        goalService.updateGoal(goalId, goalDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete goal")
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get goals by user id")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GoalDto>> getGoalsByUserId(@PathVariable Long userId,
                                                          @RequestBody GoalFilterDto filters) {
        return ResponseEntity.ok(goalService.getGoalsByUserId(userId, filters));
    }

    @Operation(summary = "Find subtasks by goal id")
    @GetMapping("/{goalId}/subtasks")
    public ResponseEntity<List<GoalDto>> findSubtasksByGoalId(@PathVariable Long goalId,
                                                              @RequestBody GoalFilterDto filters) {
        return ResponseEntity.ok(goalService.findSubtasksByGoalId(goalId, filters));
    }
}

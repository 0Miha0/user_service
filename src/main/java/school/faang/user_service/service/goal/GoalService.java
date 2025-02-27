package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.event_drive.redis.event.CompletedTaskEvent;
import school.faang.user_service.event_drive.redis.publisher.CompletedTaskEventPublisher;
import school.faang.user_service.exception.customexception.DataValidationException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.event_drive.app_event.publisher.UserDeactivationEvent;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.GoalValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;
    private final SkillService skillService;
    private final UserService userService;
    private final List<GoalFilter> goalFilters;
    private final CompletedTaskEventPublisher completedTaskEventPublisher;

    @Value("${app.user-service.goal-service.max-goals-amount}")
    private int maxGoalsAmount;

    public void createGoal(Long userId, GoalDto goalDto) {
        log.info("The beginning creation goal with id: {}", goalDto.getId());
        validateUserExistence(userId);
        validateUserGoalsAmount(userId, maxGoalsAmount);
        validateSkillsExistence(goalDto.getSkillIds());

        Goal createdGoal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        save(createdGoal);
        log.info("Goal with id: {} successfully created", goalDto.getId());
    }

    public void deleteGoal(Long goalId) {
        log.info("The beginning delete goal with id: {}", goalId);
        goalRepository.deleteById(goalId);
        log.info("Goal wit id: {} successfully deleted", goalId);
    }

    public void updateGoal(Long goalId, GoalDto goalDto) {
        log.info("The beginning update goal with id: {}", goalId);
        Goal foundGoal = findById(goalId);
        goalMapper.updateEntityFromDto(goalDto, foundGoal);
        goalValidator.validateGoalStatus(goalDto.getStatus());
        validateSkillsExistence(goalDto.getSkillIds());
        foundGoal.setParent(findById(goalDto.getParentId()));
        foundGoal.setSkillsToAchieve(skillService.findAllById(goalDto.getSkillIds()));
        foundGoal.setUpdatedAt(LocalDateTime.now());
        save(foundGoal);

        if (foundGoal.getStatus() == GoalStatus.COMPLETED){
            completedTaskEventPublisher.publish(
                    CompletedTaskEvent.builder()
                            .goalId(foundGoal.getId())
                            .actorId(foundGoal.getMentor().getId())
                            .receivedAt(LocalDateTime.now())
                            .build()
            );
        }
        log.info("Goal wit id: {} successfully updated", goalId);
    }

    @Transactional
    public List<GoalDto> getGoalsByUserId(Long userId, GoalFilterDto filter) {
        log.info("The beginning get goals with filters");
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);
        List<Goal> filteredGoals = filterGoals(goals, filter).toList();

        log.info("On the specified filters found: {} goals", filteredGoals.size());
        return goalMapper.toDtoList(filteredGoals);
    }

    @Transactional
    public List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto filters) {
        log.info("The beginning get parent goals with filters");
        Stream<Goal> subtasks = goalRepository.findByParent(goalId);
        List<Goal> filteredGoals = filterGoals(subtasks, filters).toList();

        log.info("On the specified filters found: {} parentGoals", filteredGoals.size());
        return goalMapper.toDtoList(filteredGoals);
    }

    private Stream<Goal> filterGoals(Stream<Goal> goals, GoalFilterDto filters) {
        return goalFilters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(filters))
                .reduce(goals,
                        (currentStream, goalFilter) -> goalFilter.apply(currentStream, filters),
                        (s1, s2) -> s2
                );
    }

    public Goal findById(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Goal with ID: " + id + " not found"));
    }

    public void save(Goal goal) {
        goalRepository.save(goal);
    }

    public int countActiveGoalsPerUser(Long id) {
        return goalRepository.countActiveGoalsPerUser(id);
    }

    @EventListener
    public void handleUserDeactivation(UserDeactivationEvent event) {
        deleteAllById(getIdsSetGoals(userService.findById(event.getUserId()).getSetGoals()));
    }

    public List<Long> getIdsSetGoals(List<Goal> goals) {
        return goals.stream()
                .map(Goal::getId)
                .toList();
    }

    public void deleteAllById(List<Long> ids) {
        goalRepository.deleteAllById(ids);

    }

    private void validateUserExistence(Long userId) {
        if (!userService.existsById(userId)) {
            log.info("User with id '{}' does not exist", userId);
            throw new EntityNotFoundException(String.format("User with id '%s' not found", userId));
        }
    }

    private void validateUserGoalsAmount(Long userId, int maxGoalsAmount) {
        if (countActiveGoalsPerUser(userId) == maxGoalsAmount) {
            log.info("User with id '{}' already has max amount of active goals", userId);
            throw new DataValidationException("User has max amount of active goals");
        }
    }

    private void validateSkillsExistence(List<Long> skillIds) {
        if (skillIds != null && skillService.countExisting(skillIds) != skillIds.size()) {
            log.info("Couldn't find some skills by ids '{}'", skillIds);
            throw new EntityNotFoundException("Skills with some ids not found");
        }
    }

}

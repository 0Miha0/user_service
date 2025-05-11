package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.GoalStatusFilter;
import school.faang.user_service.filter.goal.GoalTitleFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Spy
    private GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private GoalService service;

    @BeforeEach
    public void setUp() {
        List<GoalFilter> filters = List.of(
                new GoalTitleFilter(),
                new GoalStatusFilter()
        );
        ReflectionTestUtils.setField(service, "goalFilters", filters);
    }

    @Test
    @DisplayName("Test goal creation")
    public void creteGoalTest() {
        GoalDto dto = GoalDto.builder()
                .skillIds(List.of(1L))
                .build();
        Goal createdGoal = Goal.builder()
                .id(1L)
                .skillsToAchieve(List.of(Skill.builder().id(1L).build()))
                .build();
        Long userId = 1L;
        ReflectionTestUtils.setField(service, "maxGoalsAmount", 3);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(0);
        when(skillRepository.countExisting(dto.getSkillIds())).thenReturn(dto.getSkillIds().size());
        when(goalRepository.create(dto.getTitle(), dto.getDescription(), dto.getParentId())).thenReturn(createdGoal);

        GoalDto result = service.createGoal(userId, dto);

        assertEquals(createdGoal.getId(), result.getId());
    }

    @Test
    @DisplayName("Test goal creation with nonexistent user id")
    public void createGoalWithNonexistentUserIdTest() {
        GoalDto dto = new GoalDto();
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.createGoal(userId, dto));
    }

    @Test
    @DisplayName("Test goal creation for user with max goals amount")
    public void createGoalForUserWithMaxGoalsAmountTest() {
        GoalDto dto = new GoalDto();
        Long userId = 1L;
        ReflectionTestUtils.setField(service, "maxGoalsAmount", 3);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);

        assertThrows(IllegalStateException.class, () -> service.createGoal(userId, dto));
    }

    @Test
    @DisplayName("Test goal creation with nonexistent skill ids")
    public void createGoalWithNonexistentSkillIdsTest() {
        GoalDto dto = GoalDto.builder()
                .skillIds(List.of(1L, 2L, 3L))
                .build();
        Long userId = 1L;
        ReflectionTestUtils.setField(service, "maxGoalsAmount", 3);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(0);
        when(skillRepository.countExisting(dto.getSkillIds())).thenReturn(1);

        assertThrows(EntityNotFoundException.class, () -> service.createGoal(userId, dto));
    }

    @Test
    @DisplayName("Test goal update")
    public void updateGoalTest() {
        GoalDto dto = GoalDto.builder()
                .skillIds(List.of(1L))
                .build();
        Long goalId = 1L;
        Goal foundGoal = Goal.builder()
                .id(goalId)
                .status(GoalStatus.ACTIVE)
                .build();
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(foundGoal));
        when(skillRepository.countExisting(dto.getSkillIds())).thenReturn(dto.getSkillIds().size());
        when(skillRepository.findAllById(dto.getSkillIds())).thenReturn(List.of(Skill.builder().id(1L).build()));
        when(goalRepository.save(foundGoal)).thenReturn(foundGoal);

        GoalDto result = service.updateGoal(goalId, dto);

        assertEquals(goalId, result.getId());
        assertEquals(foundGoal.getSkillsToAchieve().size(), result.getSkillIds().size());
    }

    @Test
    @DisplayName("Test goal update with nonexistent goal id")
    public void updateGoalWithNonexistentGoalIdTest() {
        GoalDto dto = new GoalDto();
        Long goalId = 1L;
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateGoal(goalId, dto));
    }

    @Test
    @DisplayName("Test completed goal update")
    public void updateCompletedGoalTest() {
        GoalDto dto = new GoalDto();
        Long goalId = 1L;
        Goal foundGoal = Goal.builder()
                .status(GoalStatus.COMPLETED)
                .build();
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(foundGoal));

        assertThrows(IllegalStateException.class, () -> service.updateGoal(goalId, dto));
    }

    @Test
    @DisplayName("Test goal update with nonexistent skill ids")
    public void updateGoalWithNonexistentSkillIdsTest() {
        GoalDto dto = GoalDto.builder()
                .skillIds(List.of(1L, 2L, 3L))
                .build();
        Long goalId = 1L;
        Goal foundGoal = new Goal();
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(foundGoal));
        when(skillRepository.countExisting(dto.getSkillIds())).thenReturn(0);

        assertThrows(EntityNotFoundException.class, () -> service.updateGoal(goalId, dto));
    }

    @Test
    @DisplayName("Test goal delete")
    public void deleteGoalTest() {
        Long goalId = 1L;

        service.deleteGoal(goalId);

        verify(goalRepository).deleteById(goalId);
    }

    @Test
    @DisplayName("Test find subtasks by parent goal id")
    public void findSubtasksByGoalIdTest() {
        long goalId = 1L;
        Stream<Goal> subtasks = Stream.of(
                Goal.builder()
                        .skillsToAchieve(List.of())
                        .build(),
                Goal.builder()
                        .skillsToAchieve(List.of())
                        .build()
        );
        when(goalRepository.findByParent(goalId)).thenReturn(subtasks);

        List<GoalDto> result = service.findSubtasksByGoalId(goalId, null);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Test find subtasks by parent goal id with status filter")
    public void findSubtasksByGoalIdWithStatusFilterTest() {
        long goalId = 1L;
        Stream<Goal> subtasks = Stream.of(
                Goal.builder()
                        .status(GoalStatus.COMPLETED)
                        .skillsToAchieve(List.of())
                        .build(),
                Goal.builder()
                        .status(GoalStatus.ACTIVE)
                        .skillsToAchieve(List.of())
                        .build()
        );
        GoalFilterDto filter = GoalFilterDto.builder()
                .status(GoalStatus.ACTIVE)
                .build();
        when(goalRepository.findByParent(goalId)).thenReturn(subtasks);

        List<GoalDto> result = service.findSubtasksByGoalId(goalId, filter);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Test find subtasks by parent goal id with title filter")
    public void findSubtasksByGoalIdWithTitleFilterTest() {
        long goalId = 1L;
        Stream<Goal> subtasks = Stream.of(
                Goal.builder()
                        .title("title")
                        .skillsToAchieve(List.of())
                        .build(),
                Goal.builder()
                        .title("hi!")
                        .skillsToAchieve(List.of())
                        .build()
        );
        GoalFilterDto filter = GoalFilterDto.builder()
                .titlePattern("ti")
                .build();
        when(goalRepository.findByParent(goalId)).thenReturn(subtasks);

        List<GoalDto> result = service.findSubtasksByGoalId(goalId, filter);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Test get goals by user id")
    public void getGoalsByUserIdTest() {
        long userId = 1L;
        Stream<Goal> subtasks = Stream.of(
                Goal.builder()
                        .skillsToAchieve(List.of())
                        .build(),
                Goal.builder()
                        .skillsToAchieve(List.of())
                        .build()
        );
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(subtasks);

        List<GoalDto> result = service.getGoalsByUserId(userId, null);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Test get goals by user id with status filter")
    public void getGoalsByUserIdWithStatusFilterTest() {
        long userId = 1L;
        Stream<Goal> subtasks = Stream.of(
                Goal.builder()
                        .status(GoalStatus.COMPLETED)
                        .skillsToAchieve(List.of())
                        .build(),
                Goal.builder()
                        .status(GoalStatus.ACTIVE)
                        .skillsToAchieve(List.of())
                        .build()
        );
        GoalFilterDto filter = GoalFilterDto.builder()
                .status(GoalStatus.ACTIVE)
                .build();
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(subtasks);

        List<GoalDto> result = service.getGoalsByUserId(userId, filter);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Test get goals by user id with title filter")
    public void getGoalsByUserIdWithTitleFilterTest() {
        long userId = 1L;
        Stream<Goal> subtasks = Stream.of(
                Goal.builder()
                        .title("title")
                        .skillsToAchieve(List.of())
                        .build(),
                Goal.builder()
                        .title("hi!")
                        .skillsToAchieve(List.of())
                        .build()
        );
        GoalFilterDto filter = GoalFilterDto.builder()
                .titlePattern("ti")
                .build();
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(subtasks);

        List<GoalDto> result = service.getGoalsByUserId(userId, filter);

        assertEquals(1, result.size());
    }
}

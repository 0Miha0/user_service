package school.faang.user_service.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.mapper.goal.GoalMapperImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GoalMapperTest {

    private final GoalMapper mapper = new GoalMapperImpl();

    @Test
    @DisplayName("Test dto to entity mapping")
    public void dtoToEntityTest() {
        GoalDto dto = GoalDto.builder()
                .id(1L)
                .description("description")
                .parentId(2L)
                .title("title")
                .status(GoalStatus.ACTIVE)
                .skillIds(List.of(1L, 2L, 3L))
                .build();
        Goal expectedResult = Goal.builder()
                .id(1L)
                .description("description")
                .title("title")
                .status(GoalStatus.ACTIVE)
                .build();

        Goal actualResult = mapper.dtoToEntity(dto);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @DisplayName("Test entity to dto mapping")
    public void entityToDtoTest() {
        Goal goal = Goal.builder()
                .id(1L)
                .description("description")
                .title("title")
                .status(GoalStatus.ACTIVE)
                .parent(Goal.builder().id(2L).build())
                .skillsToAchieve(List.of(Skill.builder().id(1L).build()))
                .build();
        GoalDto expectedResult = GoalDto.builder()
                .id(1L)
                .description("description")
                .title("title")
                .status(GoalStatus.ACTIVE)
                .parentId(2L)
                .skillIds(List.of(1L))
                .build();

        GoalDto actualResult = mapper.entityToDto(goal);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @DisplayName("Test entity update")
    public void updateEntityTest() {
        Goal goal = Goal.builder()
                .id(1L)
                .description("description")
                .build();
        GoalDto goalDto = GoalDto.builder()
                .id(null)
                .description("new description")
                .build();

        mapper.updateEntity(goal, goalDto);

        assertEquals(goalDto.getDescription(), goal.getDescription());
        assertNotNull(goal.getId());
    }

    @Test
    @DisplayName("Test entity list to dto list mapping")
    public void entityListToDtoListTest() {
        Goal goal = Goal.builder()
                .id(1L)
                .description("description")
                .title("title")
                .status(GoalStatus.ACTIVE)
                .parent(Goal.builder().id(2L).build())
                .skillsToAchieve(List.of(Skill.builder().id(1L).build()))
                .build();
        GoalDto dto = GoalDto.builder()
                .id(1L)
                .description("description")
                .title("title")
                .status(GoalStatus.ACTIVE)
                .parentId(2L)
                .skillIds(List.of(1L))
                .build();
        List<GoalDto> expectedResult = List.of(dto);

        List<GoalDto> actualResult = mapper.entityListToDtoList(List.of(goal));

        assertEquals(expectedResult.size(), actualResult.size());
    }
}

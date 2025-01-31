package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class GoalTitleFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return filter != null && filter.getTitlePattern() != null && !filter.getTitlePattern().isBlank();
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filter) {
        Objects.requireNonNull(goals, "Goal stream cannot be null");
        Objects.requireNonNull(filter, "Goal filter cannot be null");

        return goals.filter(goal -> goal.getTitle().contains(filter.getTitlePattern()));
    }
}

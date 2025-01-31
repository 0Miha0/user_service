package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {

    private Long id;

    @NotBlank
    @NotNull
    @Size(max = 4096)
    private String description;
    private Long parentId;

    @NotBlank
    @Size(max = 64)
    private String title;

    @NotNull
    private GoalStatus status;
    private List<Long> skillIds;
}

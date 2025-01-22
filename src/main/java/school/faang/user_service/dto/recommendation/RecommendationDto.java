package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.dto.skill.SkillOfferDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {
    private Long id;

    @NotNull
    private Long authorId;

    @NotNull
    private Long receiverId;

    @NotNull
    private String content;

    private List<SkillOfferDto> skillOffers;

    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

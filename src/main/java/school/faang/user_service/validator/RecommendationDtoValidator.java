package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationDtoValidator {
    private static final int MONTH_DELAY_BETWEEN_RECOMMENDATIONS = 6;
    private final RecommendationRepository recRepository;
    private final SkillRepository skillRepository;

    public void validateExistedSkillsAndDate(RecommendationDto recDto) {
        checkSkillOfferExists(recDto);
        checkDateTimeRecommendationOlderSixMonth(recDto);
    }

    private void checkSkillOfferExists(RecommendationDto recDto) {
        if (recDto.getSkillOffers() == null || recDto.getSkillOffers().isEmpty()) {
            throw new NoSuchElementException("Skill offers is empty! Please provide at least one skill offer.!");
        } else {
            List<String> skillTitlesList = recDto.getSkillOffers().stream()
                    .map(SkillOfferDto::getSkillTitle)
                    .toList();

            for (String skillTitle : skillTitlesList) {
                if (!skillRepository.existsByTitle(skillTitle)) {
                    log.error("Skill with title - {} does not exist in the system!", skillTitle);
                    throw new DataValidationException("Skill with title - " + skillTitle + " does not exist in the system!");
                }
            }
        }
    }

    private void checkDateTimeRecommendationOlderSixMonth(RecommendationDto recDto) {
        recRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recDto.getAuthorId(),
                recDto.getReceiverId()).ifPresent(recommendation -> {
            if (recommendation.getCreatedAt().isAfter(recDto.getCreatedAt().minusMonths(MONTH_DELAY_BETWEEN_RECOMMENDATIONS))) {
                throw new DataValidationException("Recommendation for the same user and receiver cannot be submitted within 6 months.!");
            }
        });
    }
}

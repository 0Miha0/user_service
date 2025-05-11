package school.faang.user_service.validator.recommendation;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationDtoValidatorTest {

    private static final String EXISTING_SKILL_TITLE = "Skill";
    private static final String NO_EXISTING_SKILL_TITLE = "UnbelievableSkill";

    @InjectMocks
    private RecommendationDtoValidator recommendationDtoValidator;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillRepository skillRepository;

    private RecommendationDto getTestRecommendationData() {
        return RecommendationDto.builder()
                .content("Content")
                .authorId(1L)
                .receiverId(2L)
                .skillOffers(List.of(
                        SkillOfferDto.builder().skillTitle(EXISTING_SKILL_TITLE).build(),
                        SkillOfferDto.builder().skillTitle(NO_EXISTING_SKILL_TITLE).build()
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Throws exception if skill offers is null")
    public void whenSkillOffersIsNullThenThrowExceptionTest() {
        RecommendationDto recDto = RecommendationDto.builder()
                .skillOffers(null)
                .build();

        assertThrows(NoSuchElementException.class,
                () -> recommendationDtoValidator.validateExistedSkillsAndDate(recDto));
    }

    @Test
    @DisplayName("Throws exception if skill offers is empty")
    public void whenSkillOffersIsEmptyThenThrowExceptionTest() {
        RecommendationDto recDto = RecommendationDto.builder()
                .skillOffers(List.of())
                .build();

        assertThrows(NoSuchElementException.class,
                () -> recommendationDtoValidator.validateExistedSkillsAndDate(recDto));
    }

    @Test
    @DisplayName("Success if skill offers is not empty")
    public void whenSkillOffersIsNotEmptyTest() {
        RecommendationDto recDto = getTestRecommendationData();

        when(skillRepository.existsByTitle(EXISTING_SKILL_TITLE)).thenReturn(true);
        when(skillRepository.existsByTitle(NO_EXISTING_SKILL_TITLE)).thenReturn(true);

        recommendationDtoValidator.validateExistedSkillsAndDate(recDto);

        verify(skillRepository, times(1)).existsByTitle(EXISTING_SKILL_TITLE);
    }

    @Test
    @DisplayName("Throws exception if a skill title does not exist")
    public void whenSkillTitleDoesNotExistThenThrowExceptionTest() {
        RecommendationDto recDto = getTestRecommendationData();
        when(skillRepository.existsByTitle(EXISTING_SKILL_TITLE)).thenReturn(true);
        when(skillRepository.existsByTitle(NO_EXISTING_SKILL_TITLE)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> recommendationDtoValidator.validateExistedSkillsAndDate(recDto));
    }

    @Test
    @DisplayName("Success if all skill titles validate")
    public void whenSkillTitlesExistTest() {
        RecommendationDto recDto = getTestRecommendationData();
        when(skillRepository.existsByTitle(EXISTING_SKILL_TITLE)).thenReturn(true);
        when(skillRepository.existsByTitle(NO_EXISTING_SKILL_TITLE)).thenReturn(true);

        recommendationDtoValidator.validateExistedSkillsAndDate(recDto);

        verify(skillRepository).existsByTitle(EXISTING_SKILL_TITLE);
        verify(skillRepository).existsByTitle(NO_EXISTING_SKILL_TITLE);
    }

    @Test
    @DisplayName("Throws exception if the given recommendation is earlier than 6 months")
    public void whenValidateWrongDateMonthThenThrowExceptionTest() {
        RecommendationDto recDto = getTestRecommendationData();

        assertThrows(DataValidationException.class,
                () -> recommendationDtoValidator.validateExistedSkillsAndDate(recDto));
    }


    @Test
    @DisplayName("Success if the given recommendation is after 6 months")
    public void whenValidateCorrectDateMonthTest() {
        RecommendationDto recDto = getTestRecommendationData();

        Recommendation recommendation = Recommendation.builder()
                .createdAt(LocalDateTime.now().minusDays(186))
                .build();
        for (SkillOfferDto skillOffer : recDto.getSkillOffers()) {
            when(skillRepository.existsByTitle(skillOffer.getSkillTitle())).thenReturn(true);
        }
        recommendationDtoValidator.validateExistedSkillsAndDate(recDto);

        verify(recommendationRepository, times(1))
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recDto.getAuthorId(),
                        recDto.getReceiverId());
        long monthsBetween = ChronoUnit.MONTHS.between(recommendation.getCreatedAt(), recDto.getCreatedAt());
        assertEquals(6, monthsBetween);
    }
}

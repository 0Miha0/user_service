package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.redis.publisher.RecommendationEventPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.recommendation.RecommendationDtoValidator;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private RecommendationDtoValidator recommendationDtoValidator;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private RecommendationEventPublisher recommendationEventPublisher;

    @Spy
    private RecommendationMapper recommendationMapper = Mappers.getMapper(RecommendationMapper.class);

    private RecommendationDto getDataRecDto() {
        return RecommendationDto.builder()
                .id(1L)
                .receiverId(1L)
                .authorId(1L)
                .content("Content")
                .skillOffers(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Successful creation of a recommendation")
    public void createSuccessTest() {
        RecommendationDto recDto = getDataRecDto();
        Recommendation recommendation = recommendationMapper.toEntity(recDto);

        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);

        RecommendationDto resultRecommendationDto = recommendationService.create(recDto);

        verify(recommendationDtoValidator).validateExistedSkillsAndDate(recDto);
        verify(recommendationRepository).save(eq(recommendation));

        assertNotNull(resultRecommendationDto);
    }

    @Test
    @DisplayName("Successful update of a recommendation")
    public void updateThenSuccessTest() {
        RecommendationDto recDto = getDataRecDto();
        Recommendation recommendation = recommendationMapper.toEntity(recDto);

        when(recommendationRepository.save(recommendation)).thenReturn(recommendation);
        doNothing().when(skillOfferRepository).deleteAllByRecommendationId(recDto.getId());

        RecommendationDto result = recommendationService.update(recDto);

        assertEquals(recDto, result);
        assertEquals(recDto.getContent(), recommendation.getContent());
        assertEquals(recDto.getAuthorId(), recommendation.getAuthor().getId());
        assertEquals(recDto.getReceiverId(), recommendation.getReceiver().getId());

        verify(recommendationDtoValidator).validateExistedSkillsAndDate(recDto);
        verify(recommendationRepository).save(eq(recommendation));
    }

    @Test
    @DisplayName("Delete recommendation")
    void deleteRecommendationTest() {
        recommendationService.delete(1L);
        verify(recommendationRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Successfully get all users")
    public void whenSuccessfullyGetAllUserRecommendationsTest() {
        List<Recommendation> recommendations = List.of(new Recommendation(), new Recommendation());

        when(recommendationRepository.findListByReceiverId(1L))
                .thenReturn(recommendations);

        List<RecommendationDto> recDtos = recommendationMapper.toDtoList(recommendations);
        List<RecommendationDto> resultRecommendationDtos =
                recommendationService.getAllUserRecommendations(1L);

        assertEquals(recDtos, resultRecommendationDtos);
        verify(recommendationRepository).findListByReceiverId(1L);
    }

    @Test
    @DisplayName("Successfully get all author's recommendations")
    public void whenSuccessfullyGetAllGivenRecommendationsTest() {
        List<Recommendation> recommendations = List.of(new Recommendation(), new Recommendation());

        when(recommendationRepository.findListByReceiverId(1L))
                .thenReturn(recommendations);
        List<RecommendationDto> recDtos = recommendationMapper.toDtoList(recommendations);

        List<RecommendationDto> resultRecommendationDtos =
                recommendationService.getAllUserRecommendations(1L);

        assertEquals(recDtos, resultRecommendationDtos);
        verify(recommendationRepository).findListByReceiverId(1L);
    }
}

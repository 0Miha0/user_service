package school.faang.user_service.validator.recommendationRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestValidatorTest {

    @InjectMocks
    private RecommendationRequestValidator recommendationRequestValidator;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    private RecommendationRequestDto recommendationRequestDto;

    @BeforeEach
    public void setUp() {
        recommendationRequestDto = RecommendationRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .skillsId(List.of(1L, 2L, 3L))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Validate create with valid data")
    public void validateCreateWithValidDataTest() {
        Skill skill1 = Skill.builder()
                .id(1L)
               .build();
        Skill skill2 = Skill.builder()
                .id(2L)
                .build();
        Skill skill3 = Skill.builder()
                .id(3L)
                .build();

        RecommendationRequest existingRequest = new RecommendationRequest();
        existingRequest.setCreatedAt(LocalDateTime.now().minusMonths(7));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong()))
                .thenReturn(Optional.of(existingRequest));
        when(skillRepository.findAllById(anyList())).thenReturn(List.of(skill1, skill2, skill3));

        recommendationRequestValidator.validateCreate(recommendationRequestDto);
    }


    @Test
    @DisplayName("Validate create with non-existent user")
    public void validateCreateWithNonExistentUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () ->
                recommendationRequestValidator.validateCreate(recommendationRequestDto));
    }

    @Test
    @DisplayName("Validate no recent request")
    public void validateNoRecentRequestTest() {
        final int MONTHS_AGO_7 = 7;
        RecommendationRequest request = new RecommendationRequest();
        request.setCreatedAt(LocalDateTime.now().minusMonths(MONTHS_AGO_7));
        when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong()))
                .thenReturn(Optional.of(request));

        recommendationRequestValidator.validateNoRecentRequest(recommendationRequestDto, recommendationRequestRepository);
    }

    @Test
    @DisplayName("Validate recent request exists")
    public void validateRecentRequestExistsTest() {
        final int MONTHS_AGO_5 = 5;
        RecommendationRequest request = new RecommendationRequest();
        request.setCreatedAt(LocalDateTime.now().minusMonths(MONTHS_AGO_5));
        when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong()))
                .thenReturn(Optional.of(request));

        assertThrows(DataValidationException.class, () ->
                recommendationRequestValidator.validateNoRecentRequest(recommendationRequestDto, recommendationRequestRepository));
    }

    @Test
    @DisplayName("Validate skills with valid data")
    public void validateSkillsWithValidDataTest() {
        Skill skill1 = Skill.builder()
                .id(1L)
                .build();
        Skill skill2 = Skill.builder()
                .id(2L)
                .build();
        Skill skill3 = Skill.builder()
                .id(3L)
                .build();

        when(skillRepository.findAllById(anyList())).thenReturn(List.of(skill1, skill2, skill3));

        recommendationRequestValidator.validateSkills(recommendationRequestDto.getSkillsId(), skillRepository);
    }

    @Test
    @DisplayName("Validate skills with non-existent skill")
    public void validateSkillsWithNonExistentSkillTest() {
        when(skillRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        assertThrows(DataValidationException.class, () ->
                recommendationRequestValidator.validateSkills(recommendationRequestDto.getSkillsId(), skillRepository));
    }

    @Test
    @DisplayName("Validate requester and receiver not null")
    public void validateRequesterAndReceiverNotNullTest() {
        assertThrows(DataValidationException.class, () ->
                recommendationRequestValidator.validateRequesterAndReceiverNotNull(null));
    }

    @Test
    @DisplayName("Validate user exists")
    public void validateUserExistsTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () ->
                recommendationRequestValidator.validateUserExists(1L, userRepository));
    }

    @Test
    @DisplayName("Validate filter availability")
    public void validateOfFilterAvailabilityTest() {
        assertThrows(DataValidationException.class, () ->
                recommendationRequestValidator.validateOfFilterAvailability(null));
    }

    @Test
    @DisplayName("Validate request exists")
    public void validateRequestExistsTest() {
        when(recommendationRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () ->
                recommendationRequestValidator.validateRequestExists(1L, recommendationRequestRepository));
    }

    @Test
    @DisplayName("Validate request status rejected")
    public void validateRequestStatusRejectedTest() {
        RecommendationRequest request = new RecommendationRequest();
        request.setStatus(RequestStatus.REJECTED);

        assertThrows(DataValidationException.class, () ->
                recommendationRequestValidator.validateRequestStatus(request));
    }

    @Test
    @DisplayName("Validate request status processed")
    public void validateRequestStatusProcessedTest() {
        RecommendationRequest request = new RecommendationRequest();
        request.setStatus(RequestStatus.ACCEPTED);

        assertThrows(DataValidationException.class, () ->
                recommendationRequestValidator.validateRequestStatus(request));
    }
}

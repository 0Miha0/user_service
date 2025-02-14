package school.faang.user_service.service.recommendationRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestDto;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestRejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.customexception.DataValidationException;
import school.faang.user_service.filter.recommendationRequest.RecommendationRequestFilter;
import school.faang.user_service.mapper.recommendationRequest.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.skill.SkillRequestService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final SkillRequestService skillRequestService;
    private final UserService userService;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        log.info("Creating a recommendation request for requester ID: {} and recipient ID: {}",
                recommendationRequestDto.getRequester(), recommendationRequestDto.getReceiver());

        recommendationRequestValidator.validateCreate(recommendationRequestDto);

        RecommendationRequest recommendationRequestEntity = RecommendationRequest.builder()
                .message(recommendationRequestDto.getMessage())
                .status(RequestStatus.PENDING)
                .skills(skillRequestService.findAllById(recommendationRequestDto.getSkills()))
                .requester(userService.findById(recommendationRequestDto.getRequester()))
                .receiver(userService.findById(recommendationRequestDto.getReceiver()))
                .createdAt(LocalDateTime.now())
                .build();
        recommendationRequestRepository.save(recommendationRequestEntity);
        log.info("Recommendation request saved with ID: {}", recommendationRequestEntity.getId());

        skillRequestService.saveSkillRequests(recommendationRequestEntity, recommendationRequestDto.getSkills());

        log.info("Recommendation request successfully created with ID: {}", recommendationRequestEntity.getId());
        return recommendationRequestMapper.toDto(recommendationRequestEntity);
    }

    public List<RecommendationRequestDto> getRequest(RecommendationRequestFilterDto recommendationRequestFilterDto) {
        log.info("Retrieving recommendation requests with filters: {}", recommendationRequestFilterDto);

        recommendationRequestValidator.validateOfFilterAvailability(recommendationRequestFilterDto);

        Stream<RecommendationRequest> recommendationRequestStream = recommendationRequestRepository.findAll().stream();
        recommendationRequestStream = recommendationRequestFilters.stream()
                .filter(requestFilter -> requestFilter.isApplicable(recommendationRequestFilterDto))
                .reduce(recommendationRequestStream,
                        (currentStream, requestFilter) -> requestFilter.apply(currentStream, recommendationRequestFilterDto),
                        (s1, s2) -> s2);

        List<RecommendationRequest> result = recommendationRequestStream.toList();

        log.info("Returned {} recommendation requests after applying filters", result.size());
        return recommendationRequestMapper.toDtoList(result);
    }

    public RecommendationRequestDto getRequest(Long id) {
        log.info("Retrieving a recommendation request with ID: {}", id);
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Recommendation request with ID {} not found", id);
                    return new DataValidationException("Recommendation request with ID " + id + " not found");
                });
        log.info("Recommendation request found with ID: {}", id);
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    public RecommendationRequestDto rejectRequest(Long id, RecommendationRequestRejectionDto recommendationRequestRejectionDto) {
        log.info("Rejecting recommendation request with ID: {}", id);

        RecommendationRequest recommendationRequest = recommendationRequestValidator.validateRequestExists(id,
                recommendationRequestRepository);

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(recommendationRequestRejectionDto.getRejectionReason());
        recommendationRequestRepository.save(recommendationRequest);
        log.info("Recommendation request with ID: {} successfully rejected", id);

        return recommendationRequestMapper.toDto(recommendationRequest);
    }
}


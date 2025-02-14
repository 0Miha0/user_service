package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestCreationDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.customexception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MentorshipRequestDtoValidator {
    public static final int MIN_REQUEST_INTERVAL = 3;

    private final UserService userService;
    private final MentorshipRequestRepository requestRepository;

    public void validateCreationRequest(MentorshipRequestCreationDto creationRequestDto) {
        Long requesterId = creationRequestDto.getRequesterId();
        Long receiverId = creationRequestDto.getReceiverId();

        validateUserExistence(requesterId, receiverId);
        validateDifferentUsers(requesterId, receiverId);
        validateLastRequestDate(requesterId, receiverId);
    }

    public MentorshipRequest validateAcceptRequest(Long requestId) {
        MentorshipRequest request = validateRequest(requestId);
        if (requestRepository.existAcceptedRequest(request.getRequester().getId(), request.getReceiver().getId())) {
            throw new DataValidationException(
                    "User %d and user %d already have an active mentorship relationship!".formatted(
                            request.getRequester().getId(), request.getReceiver().getId()
                    )
            );
        }
        return request;
    }

    public MentorshipRequest validateRejectRequest(Long requestId) {
        return validateRequest(requestId);
    }

    private void validateUserExistence(Long... userIds) {
        Arrays.stream(userIds)
                .forEach(userId -> {
                    if (!userService.existsById(userId)) {
                        throw new DataValidationException("User with ID %d does not exist in the database!".formatted(userId));
                    }
                });
    }

    private void validateDifferentUsers(Long firstUserId, Long secondUserId) {
        if (firstUserId.equals(secondUserId)) {
            throw new DataValidationException(
                    "The requester and receiver of a mentorship request cannot have the same ID. User ID: %d"
                            .formatted(firstUserId)
            );
        }
    }

    private void validateLastRequestDate(Long requesterId, Long receiverId) {
        Optional<MentorshipRequest> request = requestRepository.findLatestRequest(requesterId, receiverId);
        if (request.isPresent()) {
            LocalDateTime dateThreshold = LocalDateTime.now().minusMonths(MIN_REQUEST_INTERVAL);
            if (request.get().getCreatedAt().isBefore(dateThreshold)) {
                throw new DataValidationException(
                        "The last mentorship request from user %d to user %d was sent less than %d months ago!"
                                .formatted(requesterId, receiverId, MIN_REQUEST_INTERVAL));
            }
        }
    }

    private MentorshipRequest validateRequest(Long requestId) {
        validateIdIsNotNull(requestId);
        MentorshipRequest request = validateRequestExistence(requestId);
        validateRequestIsPending(request);

        return request;
    }

    private void validateRequestIsPending(MentorshipRequest request) {
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new DataValidationException("The mentorship request with ID %d has already been processed!".formatted(request.getId()));
        }
    }

    private MentorshipRequest validateRequestExistence(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new DataValidationException(
                        "The mentorship request with ID %d does not exist in the database!".formatted(requestId))
                );
    }

    private void validateIdIsNotNull(Long id) {
        if (id == null) {
            throw new DataValidationException("ID must not be null!");
        }
    }
}


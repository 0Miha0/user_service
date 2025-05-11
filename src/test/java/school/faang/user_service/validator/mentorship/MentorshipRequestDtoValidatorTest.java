package school.faang.user_service.validator.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestCreationDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestDtoValidatorTest {

    @Mock
    private MentorshipRequestRepository requestRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private MentorshipRequestDtoValidator validator;

    private Long baseRequestId;
    private Long baseRequesterId;
    private Long baseReceiverId;
    private User baseRequester;
    private User baseReceiver;
    private MentorshipRequestCreationDto requestCreationDto;
    private MentorshipRequest baseRequest;

    @BeforeEach
    public void setUp() {
        baseRequestId = 10L;

        baseRequesterId = 1L;
        baseReceiverId = 2L;

        baseRequester = new User();
        baseRequester.setId(baseRequesterId);
        baseReceiver = new User();
        baseReceiver.setId(baseReceiverId);

        requestCreationDto = MentorshipRequestCreationDto.builder()
                .description("Some description :)")
                .requesterId(baseRequesterId)
                .receiverId(baseReceiverId)
                .build();

        baseRequest = new MentorshipRequest();
        baseRequest.setStatus(RequestStatus.PENDING);
        baseRequest.setId(baseRequestId);
        baseRequest.setRequester(baseRequester);
        baseRequest.setReceiver(baseReceiver);
    }

    @Test
    void validateCreationRequestNotExistingUserTest() {
        when(userService.existsById(baseRequesterId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> validator.validateCreationRequest(requestCreationDto)
        );

        verify(userService, times(1)).existsById(baseRequesterId);
        assertEquals("User with ID 1 does not exist in the database!", exception.getMessage());
    }

    @Test
    void validateCreationRequestSameUsersTest() {
        requestCreationDto = requestCreationDto.toBuilder()
                .receiverId(baseRequesterId)
                .build();
        when(userService.existsById(baseRequesterId)).thenReturn(true);

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> validator.validateCreationRequest(requestCreationDto)
        );
        assertEquals(
                "The requester and receiver of a mentorship request cannot have the same ID. User ID: %d".formatted(baseRequesterId),
                exception.getMessage()
        );
        verify(userService, times(2)).existsById(baseRequesterId);
    }

    @Test
    void validateCreationRequestInvalidDateTest() {
        baseRequest.setCreatedAt(LocalDateTime.now().minusMonths(MentorshipRequestDtoValidator.MIN_REQUEST_INTERVAL + 1));

        when(userService.existsById(baseRequesterId)).thenReturn(true);
        when(userService.existsById(baseReceiverId)).thenReturn(true);
        when(requestRepository.findLatestRequest(baseRequesterId, baseReceiverId)).thenReturn(Optional.of(baseRequest));

        assertThrows(DataValidationException.class, () -> validator.validateCreationRequest(requestCreationDto));
        verify(userService, times(1)).existsById(baseRequesterId);
        verify(userService, times(1)).existsById(baseReceiverId);
        verify(requestRepository, times(1)).findLatestRequest(baseRequesterId, baseReceiverId);
    }

    @Test
    void validateCreationRequestValidTest() {
        baseRequest.setCreatedAt(LocalDateTime.now().minusMonths(MentorshipRequestDtoValidator.MIN_REQUEST_INTERVAL - 1));

        when(userService.existsById(baseRequesterId)).thenReturn(true);
        when(userService.existsById(baseReceiverId)).thenReturn(true);
        when(requestRepository.findLatestRequest(baseRequesterId, baseReceiverId)).thenReturn(Optional.of(baseRequest));

        assertDoesNotThrow(() -> validator.validateCreationRequest(requestCreationDto));
        verify(userService, times(1)).existsById(baseRequesterId);
        verify(userService, times(1)).existsById(baseReceiverId);
        verify(requestRepository, times(1)).findLatestRequest(baseRequesterId, baseReceiverId);
    }

    @Test
    void validateAcceptRequestWithNullIdTest() {
        assertNullRequestId(() -> validator.validateAcceptRequest(null));
    }

    @Test
    void validateAcceptNotExistingRequestTest() {
        assertRequestNotExisting(() -> validator.validateAcceptRequest(baseRequestId));
    }

    @Test
    void validateAcceptNotPendingRequestTest() {
        assertNotPendingRequest(() -> validator.validateAcceptRequest(baseRequestId));
    }

    @Test
    void validateAcceptRequestMentorshipAlreadyExists() {
        when(requestRepository.findById(baseRequestId)).thenReturn(Optional.of(baseRequest));
        when(requestRepository.existAcceptedRequest(baseRequesterId, baseReceiverId)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> validator.validateAcceptRequest(baseRequestId));
        verify(requestRepository, times(1)).findById(baseRequestId);
        verify(requestRepository, times(1)).existAcceptedRequest(baseRequesterId, baseReceiverId);
    }

    @Test
    void validateAcceptValidRequestTest() {
        when(requestRepository.existAcceptedRequest(baseRequesterId, baseReceiverId)).thenReturn(false);

        assertValidRequestTest(() -> validator.validateAcceptRequest(baseRequestId));
        verify(requestRepository, times(1)).existAcceptedRequest(baseRequesterId, baseReceiverId);
    }

    @Test
    void validateRejectRequestWithNullIdTest() {
        assertNullRequestId(() -> validator.validateRejectRequest(null));
    }

    @Test
    void validateRejectNotExistingRequestTest() {
        assertRequestNotExisting(() -> validator.validateRejectRequest(baseRequestId));
    }

    @Test
    void validateRejectNotPendingRequestTest() {
        assertNotPendingRequest(() -> validator.validateRejectRequest(baseRequestId));
    }

    @Test
    void validateRejectValidRequestTest() {
        assertValidRequestTest(() -> validator.validateRejectRequest(baseRequestId));
    }

    private void assertNullRequestId(Executable validatorMethod) {
        DataValidationException exception = assertThrows(
                DataValidationException.class,
                validatorMethod
        );
        assertEquals("ID must not be null!", exception.getMessage());
    }

    private void assertRequestNotExisting(Executable validatorMethod) {
        when(requestRepository.findById(baseRequestId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, validatorMethod);
        assertEquals("The mentorship request with ID %d does not exist in the database!".formatted(baseRequestId), exception.getMessage());
        verify(requestRepository, times(1)).findById(baseRequestId);
    }

    private void assertNotPendingRequest(Executable validatorMethod) {
        baseRequest.setStatus(RequestStatus.ACCEPTED);
        when(requestRepository.findById(baseRequestId)).thenReturn(Optional.of(baseRequest));

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                validatorMethod
        );
        assertEquals("The mentorship request with ID %d has already been processed!".formatted(baseRequest.getId()), exception.getMessage());
        verify(requestRepository, times(1)).findById(baseRequestId);
    }

    private void assertValidRequestTest(Executable validatorMethod) {
        when(requestRepository.findById(baseRequestId)).thenReturn(Optional.of(baseRequest));

        assertDoesNotThrow(validatorMethod);

        verify(requestRepository, times(1)).findById(baseRequestId);
    }
}
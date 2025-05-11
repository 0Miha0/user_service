package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestCreationDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentorship.RequestDescriptionFilter;
import school.faang.user_service.filter.mentorship.RequestFilter;
import school.faang.user_service.filter.mentorship.RequestReceiverFilter;
import school.faang.user_service.filter.mentorship.RequestRequesterFilter;
import school.faang.user_service.filter.mentorship.RequestStatusFilter;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapperImpl;
import school.faang.user_service.redis.publisher.MentorshipAcceptedEventPublisher;
import school.faang.user_service.redis.publisher.MentorshipRequestedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.mentorship.MentorshipRequestDtoValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository requestRepository;

    @Mock
    private UserService userService;

    @Mock
    private MentorshipRequestDtoValidator requestValidator;

    @Spy
    private MentorshipRequestMapperImpl requestMapper;

    @Mock
    MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;

    @Mock
    MentorshipRequestedEventPublisher mentorshipRequestedEventPublisher;

    @Captor
    ArgumentCaptor<MentorshipRequest> requestCaptor;

    @Captor
    ArgumentCaptor<List<MentorshipRequest>> requestListCaptor;

    private MentorshipRequestService requestService;

    private MentorshipRequest request;
    private MentorshipRequestCreationDto creationDto;

    private MentorshipRequest firstRequest;
    private MentorshipRequest secondRequest;
    private MentorshipRequest thirdRequest;
    private MentorshipRequest fourthRequest;

    private RequestFilterDto complexFilter;
    private RequestFilterDto emptyFilter;

    private List<MentorshipRequest> requests;

    @BeforeEach
    public void setup() {
        RequestFilter firstFilter = Mockito.spy(RequestDescriptionFilter.class);
        RequestFilter secondFilter = Mockito.spy(RequestRequesterFilter.class);
        RequestFilter thirdFilter = Mockito.spy(RequestReceiverFilter.class);
        RequestFilter fourthFilter = Mockito.spy(RequestStatusFilter.class);
        List<RequestFilter> requestFilters = List.of(firstFilter, secondFilter, thirdFilter, fourthFilter);

        requestService = new MentorshipRequestService(
                requestRepository,
                userService,
                requestValidator,
                requestMapper,
                requestFilters,
                mentorshipAcceptedEventPublisher,
                mentorshipRequestedEventPublisher
        );

        Long requesterId = 1L;
        Long receiverId = 2L;
        String description = "some description";
        creationDto = MentorshipRequestCreationDto.builder()
                .description(description)
                .requesterId(requesterId)
                .receiverId(receiverId)
                .build();

        request = requestMapper.toMentorshipRequest(creationDto);
        request.setStatus(RequestStatus.PENDING);

        complexFilter = new RequestFilterDto("Request", 1L, 2L, RequestStatus.PENDING);
        emptyFilter = new RequestFilterDto(null, null, null, null);

        setupRequestsList();
    }

    @Test
    void requestMentorshipValidTest() {
        when(requestRepository.save(any(MentorshipRequest.class))).thenReturn(request);

        assertDoesNotThrow(() -> requestService.requestMentorship(creationDto));

        verify(requestValidator, times(1)).validateCreationRequest(creationDto);
        verify(mentorshipRequestedEventPublisher, times(1)).publish(any());
        verify(requestRepository, times(1)).save(requestCaptor.capture());
        MentorshipRequest savedRequest = requestCaptor.getValue();
        assertEquals(creationDto.getDescription(), savedRequest.getDescription());
        assertEquals(creationDto.getRequesterId(), savedRequest.getRequester().getId());
        assertEquals(creationDto.getReceiverId(), savedRequest.getReceiver().getId());
    }

    @Test
    void getRequestsEmptyFilter() {
        when(requestRepository.findAll()).thenReturn(requests);

        List<MentorshipRequestDto> filteredRequests = requestService.getRequests(emptyFilter);

        verify(requestRepository, times(1)).findAll();
        assertEquals(requests.size(), filteredRequests.size());
    }

    @Test
    void getRequestsComplexFilter() {
        when(requestRepository.findAll()).thenReturn(requests);

        requestService.getRequests(complexFilter);

        verify(requestRepository, times(1)).findAll();
        verify(requestMapper, times(1)).toDtoList(requestListCaptor.capture());
        assertEquals(1, requestListCaptor.getValue().size());
        assertTrue(requestListCaptor.getValue().contains(firstRequest));
    }

    @Test
    void getRequestsWithEmptyRequests() {
        when(requestRepository.findAll()).thenReturn(Collections.emptyList());

        List<MentorshipRequestDto> filteredRequests = requestService.getRequests(emptyFilter);

        verify(requestRepository, times(1)).findAll();
        assertEquals(0, filteredRequests.size());
    }

    @Test
    void acceptRequestValidTest() {
        Long requestId = 1L;
        firstRequest.getRequester().setMentees(null);
        firstRequest.getReceiver().setMentors(null);
        when(requestValidator.validateAcceptRequest(requestId)).thenReturn(firstRequest);
        when(requestRepository.save(any(MentorshipRequest.class))).thenReturn(firstRequest);
        doNothing().when(mentorshipAcceptedEventPublisher).publish(any());

        MentorshipRequestDto resultDto = requestService.acceptRequest(requestId);

        verify(userService, times(1)).saveUser(firstRequest.getRequester());
        verify(userService, times(1)).saveUser(firstRequest.getReceiver());
        verify(requestValidator, times(1)).validateAcceptRequest(requestId);
        verify(mentorshipAcceptedEventPublisher, times(1)).publish(any());
        assertEquals(RequestStatus.ACCEPTED, resultDto.getStatus());
        assertEquals(firstRequest.getId(), resultDto.getId());
    }

    @Test
    void rejectRequestValidTest() {
        Long requestId = 3L;
        RejectionDto rejectionDto = new RejectionDto("Not enough experience");
        when(requestValidator.validateRejectRequest(requestId)).thenReturn(firstRequest);
        when(requestRepository.save(any(MentorshipRequest.class))).thenReturn(firstRequest);

        MentorshipRequestDto result = requestService.rejectRequest(requestId, rejectionDto);

        verify(requestValidator, times(1)).validateRejectRequest(requestId);
        assertEquals(RequestStatus.REJECTED, result.getStatus());
        assertEquals("Not enough experience", result.getRejectionReason());
        assertEquals(firstRequest.getId(), result.getId());
    }

    private void setupRequestsList() {
        firstRequest =
                createRequest(1L, "Request 1", 1L, 2L, RequestStatus.PENDING, null);
        secondRequest =
                createRequest(2L, "Request 2", 3L, 4L, RequestStatus.ACCEPTED, null);
        thirdRequest =
                createRequest(3L, "Request 3", 5L, 6L, RequestStatus.REJECTED, "Not enough experience");
        fourthRequest =
                createRequest(4L, "Request 4", 7L, 8L, RequestStatus.PENDING, null);

        requests = List.of(firstRequest, secondRequest, thirdRequest, fourthRequest);
    }

    private MentorshipRequest createRequest(Long id, String description, Long requesterId, Long receiverId,
                                            RequestStatus status, String rejectionReason) {
        MentorshipRequest testRequest = new MentorshipRequest();
        testRequest.setId(id);
        testRequest.setDescription(description);
        testRequest.setRequester(User.builder().id(requesterId).username("user" + requesterId).build());
        testRequest.setReceiver(User.builder().id(receiverId).username("user" + receiverId).build());
        testRequest.setStatus(status);
        testRequest.setRejectionReason(rejectionReason);
        testRequest.setCreatedAt(LocalDateTime.now());
        testRequest.setUpdatedAt(LocalDateTime.now());
        return testRequest;
    }
}
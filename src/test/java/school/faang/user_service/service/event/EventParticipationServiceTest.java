package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    @InjectMocks
    private EventParticipationService service;

    @Mock
    private EventParticipationRepository repository;

    @Spy
    private UserMapper mapper;

    private final long eventId = 1L;
    private final long userId = 100L;

    @Test
    public void registerParticipantWithRegisteredUserTest() {
        User user = new User();
        user.setId(userId);
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(user));
        assertThrows(IllegalStateException.class, () -> service.registerParticipant(eventId, userId));
    }

    @Test
    public void registerParticipantTest() {
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(Collections.emptyList());
        service.registerParticipant(eventId, userId);
        verify(repository, times(1)).register(eventId, userId);
    }

    @Test
    void unregisterParticipantIsParticipantRegisteredTest() {
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(new ArrayList<>());
        assertThrows(IllegalStateException.class, () -> service.unregisterParticipant(eventId, userId));
    }

    @Test
    void unregisterParticipantTest() {
        User user = new User();
        user.setId(userId);
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(user));
        service.unregisterParticipant(eventId, userId);
        verify(repository, times(1)).unregister(eventId, userId);
    }

    @Test
    void getParticipantEmptyEventTest() {
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(Collections.emptyList());
        assertThrows(IllegalStateException.class, () -> service.getParticipant(eventId));
    }

    @Test
    void getParticipantsTest() {
        User user = new User();
        UserDto userDto = new UserDto();
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(user));
        doReturn(userDto).when(mapper).toDto(user);
        assertEquals(List.of(userDto), service.getParticipant(eventId));
        verify(repository, times(2)).findAllParticipantsByEventId(eventId);
        verify(mapper, times(1)).toDto(user);
    }

    @Test
    void getParticipantsCountTest() {
        when(repository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(new User()));
        when(repository.countParticipants(eventId)).thenReturn(1);
        assertEquals(1, service.getParticipantsCount(eventId));
        verify(repository, times(1)).countParticipants(eventId);
    }

}
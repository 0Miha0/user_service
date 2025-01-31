package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserParticipationDto;
import school.faang.user_service.mapper.user.UserParticipationMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository repository;
    private final UserParticipationMapper userParticipationMapper;

    @Transactional
    public void registerParticipant(Long eventId, Long userId) {
        if (checkRegistration(eventId, userId)) {
            throw new IllegalStateException("User is already registered to event");
        }
        repository.register(eventId, userId);
    }

    @Transactional
    public void unregisterParticipant(Long eventId, Long userId) {
        if (!checkRegistration(eventId, userId)) {
            throw new IllegalStateException("User is not registered to event");
        }
        repository.unregister(eventId, userId);
    }

    public List<UserParticipationDto> getParticipant(Long eventId) {
        checkEventById(eventId);
        return repository.findAllParticipantsByEventId(eventId).stream()
                .map(userParticipationMapper::toDto)
                .toList();
    }

    public Integer getParticipantsCount(Long eventId) {
        checkEventById(eventId);
        return repository.countParticipants(eventId);
    }

    private void checkEventById(Long eventId) {
        if (repository.findAllParticipantsByEventId(eventId).isEmpty()) {
            throw new IllegalStateException("There is no event with this id");
        }
    }

    private boolean checkRegistration(Long eventId, Long userId) {
        return repository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId().equals(userId));
    }
}

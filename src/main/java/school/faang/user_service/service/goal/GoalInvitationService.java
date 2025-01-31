package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterIDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.goal.invitation.InvitationFilter;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalInvitationService {

    private final GoalInvitationRepository goalInvitationRepository;
    private final GoalInvitationMapper goalInvitationMapper;
    private final List<InvitationFilter> invitationFilters;

    public void sendInvitationGoal(GoalInvitationDto dto) {
        log.info("Send goal invitation with id: {}", dto.getId());
        GoalInvitation invitation = goalInvitationMapper.toEntity(dto);
        invitation.setStatus(RequestStatus.PENDING);
        save(invitation);
        log.info("Goal invitation send with id: {}", dto.getId());
    }

    public void acceptInvitationGoal(Long id) {
        log.info("Accept goal invitation with id: {}", id);
        GoalInvitation invitation = findById(id);
        User invitedUser = invitation.getInvited();
        invitedUser.setGoals(List.of(invitation.getGoal()));
        invitation.setStatus(RequestStatus.ACCEPTED);
        save(invitation);
        log.info("Goal invitation was accepted. Invitation ID - {}", invitation.getId());
    }

    public void rejectGoalInvitation(Long id) {
        log.info("Reject goal invitation with id: {}", id);
        GoalInvitation invitation = findById(id);
        invitation.setStatus(RequestStatus.REJECTED);
        save(invitation);
        log.info("Goal invitation was rejected. Invitation ID - {}", invitation.getId());
    }

    public List<GoalInvitationDto> viewAllInvitations(GoalInvitationFilterIDto filters) {
        log.info("Search for invitations matching the provided filter...");
        Stream<GoalInvitation> invitations = goalInvitationRepository.findAll().stream();

        List<GoalInvitation> filteredInvitations = invitationFilters.stream()
                .filter(invitation -> invitation.isApplicable(filters))
                .reduce(invitations,
                        (currentStream, invitationFilter) -> invitationFilter.apply(currentStream, filters),
                        (stream1, stream2) -> stream2
                ).toList();
        log.info("Found {} invitations matching the provided filter", filteredInvitations.size());
        return goalInvitationMapper.toDtoList(filteredInvitations);
    }

    public GoalInvitation findById(Long id) {
        return goalInvitationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invitation to join the goal with id: " + id + " not found in DB"));
    }

    public void save(GoalInvitation invitation) {
        goalInvitationRepository.save(invitation);
    }
}

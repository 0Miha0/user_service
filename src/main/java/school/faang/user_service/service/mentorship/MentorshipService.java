package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Service
public class MentorshipService {

    private final UserService userService;

    public MentorshipUserDto getMentee(Long id) {
        log.info("The beginning get mentees");
        return getUsers(id, User::getMentees, "Mentees successfully retrieved");
    }

    public MentorshipUserDto getMentor(Long id) {
        log.info("The beginning get mentors");
        return getUsers(id, User::getMentors, "Mentors successfully retrieved");
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        log.info("The beginning delete mentee");
        User mentor = userService.findById(mentorId);
        mentor.getMentees().removeIf(user -> user.getId().equals(menteeId));
        userService.save(mentor);
        log.info("Mentee successfully deleted");
    }

    public void deleteMentor(Long mentorId, Long menteeId) {
        log.info("The beginning delete mentor");
        User mentee = userService.findById(menteeId);
        mentee.getMentors().removeIf(user -> user.getId().equals(mentorId));
        userService.save(mentee);
        log.info("Mentor successfully deleted");
    }

    public void deleteMentorFromMentee(List<User> menteeIds) {
        log.info("The beginning delete");
        menteeIds.forEach(mentees -> mentees.getMentors()
                .removeIf(user -> user.getId().equals(mentees.getId())));
        userService.saveAll(menteeIds);
        log.info("Successfully deleted");
    }

    private MentorshipUserDto getUsers(Long id, Function<User, List<User>> relationGetter, String successLogMessage) {
        User user = userService.findById(id);
        List<User> users = relationGetter.apply(user);

        MentorshipUserDto mentorshipUserDto = new MentorshipUserDto();
        mentorshipUserDto.setUserIds(
                users.stream()
                        .map(User::getId)
                        .toList()
        );
        log.info(successLogMessage);

        return mentorshipUserDto;
    }
}

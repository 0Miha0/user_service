package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSkillFilterTest {

    private UserSkillFilter userSkillFilter;
    private UserFilterDto filter;

    @BeforeEach
    void setUp() {
        filter = new UserFilterDto();
        userSkillFilter = new UserSkillFilter();
    }

    @Test
    public void isApplicableFilterTest() {
        filter.setSkillPattern("skill");

        assertTrue(userSkillFilter.isApplicable(filter));
    }

    @Test
    public void filterIsNullTest() {
        UserFilterDto emptyFilter = null;
        assertFalse(userSkillFilter.isApplicable(emptyFilter));
    }

    @Test
    public void skillPatternIsNullTest() {
        assertFalse(userSkillFilter.isApplicable(filter));
    }

    @Test
    public void skillPatternIsBlank() {
        filter.setSkillPattern("");

        assertFalse(userSkillFilter.isApplicable(filter));
    }

    @Test
    public void applyWithMatchingSkillPatternShouldReturnFilteredUsersTest() {
        List<Skill> skills = new ArrayList<>(List.of(mock(Skill.class)));
        List<Skill> skills2 = new ArrayList<>(List.of(mock(Skill.class)));

        User user = mock(User.class);
        when(user.getSkills()).thenReturn(skills);
        when(user.getSkills().get(0).getTitle()).thenReturn("skill");

        User user2 = mock(User.class);
        when(user2.getSkills()).thenReturn(skills2);
        when(user2.getSkills().get(0).getTitle()).thenReturn("another");

        filter.setSkillPattern("skill");
        Stream<User> users = Stream.of(user, user2);

        List<User> filteredUsers = userSkillFilter.apply(users, filter).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user));
        assertFalse(filteredUsers.contains(user2));
    }

    @Test
    public void applyWithNonMatchingSkillPatternShouldReturnEmptyStreamTest() {
        List<Skill> skills = new ArrayList<>(List.of(mock(Skill.class)));
        User user = mock(User.class);
        when(user.getSkills()).thenReturn(skills);
        when(user.getSkills().get(0).getTitle()).thenReturn("skill");

        filter.setSkillPattern("another");
        Stream<User> users = Stream.of(user);

        List<User> filteredUsers = userSkillFilter.apply(users, filter).toList();

        assertTrue(filteredUsers.isEmpty());
    }
}
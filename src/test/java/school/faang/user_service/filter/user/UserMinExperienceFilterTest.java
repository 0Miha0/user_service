package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserMinExperienceFilterTest {

    private UserMinExperienceFilter userMinExperienceFilter;
    private UserFilterDto filter;

    @BeforeEach
    void setUp() {
        filter = new UserFilterDto();
        userMinExperienceFilter = new UserMinExperienceFilter();
    }

    @Test
    public void isApplicableFilterTest() {
        filter.setExperienceMin(1);

        assertTrue(userMinExperienceFilter.isApplicable(filter));
    }

    @Test
    public void filterIsNullTest() {
        UserFilterDto emptyFilter = null;
        assertFalse(userMinExperienceFilter.isApplicable(emptyFilter));
    }

    @Test
    public void minExperienceLessThanZeroShouldReturnFalseTest() {
        filter.setExperienceMin(-1);

        assertFalse(userMinExperienceFilter.isApplicable(filter));
    }

    @Test
    public void applyWithMatchingMinExperiencePatternShouldReturnFilteredUsersTest() {
        User user = mock(User.class);
        when(user.getExperience()).thenReturn(10);
        User user2 = mock(User.class);
        when(user2.getExperience()).thenReturn(4);
        filter.setExperienceMin(5);
        Stream<User> users = Stream.of(user, user2);

        List<User> filteredUsers = userMinExperienceFilter.apply(users, filter).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user));
        assertFalse(filteredUsers.contains(user2));
    }

    @Test
    public void applyWithNonMatchingMinExperiencePatternShouldReturnEmptyStreamTest() {
        User user = mock(User.class);
        when(user.getExperience()).thenReturn(5);
        filter.setExperienceMin(10);
        Stream<User> users = Stream.of(user);

        List<User> filteredUsers = userMinExperienceFilter.apply(users, filter).toList();

        assertTrue(filteredUsers.isEmpty());
    }
}

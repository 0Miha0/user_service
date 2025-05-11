package school.faang.user_service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserMaxExperienceFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserMaxExperienceFilterTest {

    private UserMaxExperienceFilter userMaxExperienceFilter;
    private UserFilterDto filter;

    @BeforeEach
    void setUp() {
        filter = new UserFilterDto();
        userMaxExperienceFilter = new UserMaxExperienceFilter();
    }

    @Test
    public void isApplicableFilterTest() {
        filter.setExperienceMax(1);

        assertTrue(userMaxExperienceFilter.isApplicable(filter));
    }

    @Test
    public void filterIsNullTest() {
        UserFilterDto emptyFilter = null;
        assertFalse(userMaxExperienceFilter.isApplicable(emptyFilter));
    }

    @Test
    public void maxExperienceLessThanZeroShouldReturnFalseTest() {
        filter.setExperienceMax(-1);

        assertFalse(userMaxExperienceFilter.isApplicable(filter));
    }

    @Test
    public void applyWithMatchingMaxExperiencePatternShouldReturnFilteredUsersTest() {
        User user = mock(User.class);
        when(user.getExperience()).thenReturn(4);
        User user2 = mock(User.class);
        when(user2.getExperience()).thenReturn(10);


        filter.setExperienceMax(5);
        Stream<User> users = Stream.of(user, user2);

        List<User> filteredUsers = userMaxExperienceFilter.apply(users, filter).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user));
        assertFalse(filteredUsers.contains(user2));
    }

    @Test
    public void applyWithNonMatchingMaxExperiencePatternShouldReturnEmptyStreamTest() {
        User user = mock(User.class);
        when(user.getExperience()).thenReturn(10);
        filter.setExperienceMax(5);

        Stream<User> users = Stream.of(user);

        List<User> filteredUsers = userMaxExperienceFilter.apply(users, filter).toList();

        assertTrue(filteredUsers.isEmpty());
    }
}
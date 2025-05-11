package school.faang.user_service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserNameFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserNameFilterTest {

    private UserNameFilter userNameFilter;
    private UserFilterDto filter;

    @BeforeEach
    void setUp() {
        filter = new UserFilterDto();
        userNameFilter = new UserNameFilter();
    }

    @Test
    public void isApplicableFilterTest() {
        filter.setNamePattern("name");

        assertTrue(userNameFilter.isApplicable(filter));
    }

    @Test
    public void filterIsNullTest() {
        UserFilterDto emptyFilter = null;
        assertFalse(userNameFilter.isApplicable(emptyFilter));
    }

    @Test
    public void namePatternIsNullTest() {
        assertFalse(userNameFilter.isApplicable(filter));
    }

    @Test
    public void namePatternIsBlankTest() {
        filter.setNamePattern("");

        assertFalse(userNameFilter.isApplicable(filter));
    }

    @Test
    public void applyWithMatchingNamePatternShouldReturnFilteredUsersTest() {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("Petr");
        User user2 = mock(User.class);
        when(user2.getUsername()).thenReturn("name");

        filter.setNamePattern("Petr");
        Stream<User> users = Stream.of(user, user2);

        List<User> filteredUsers = userNameFilter.apply(users, filter).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user));
        assertFalse(filteredUsers.contains(user2));
    }

    @Test
    public void applyWithNonMatchingNamePatternShouldReturnEmptyStreamTest() {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("Petr");
        filter.setNamePattern("name");
        Stream<User> users = Stream.of(user);

        List<User> filteredUsers = userNameFilter.apply(users, filter).toList();

        assertTrue(filteredUsers.isEmpty());
    }
}
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
class UserPhoneFilterTest {

    private UserPhoneFilter userPhoneFilter;
    private UserFilterDto filter;

    @BeforeEach
    void setUp() {
        filter = new UserFilterDto();
        userPhoneFilter = new UserPhoneFilter();
    }

    @Test
    public void isApplicableFilterTest() {
        filter.setPhonePattern("999");

        assertTrue(userPhoneFilter.isApplicable(filter));
    }

    @Test
    public void filterIsNullTest() {
        UserFilterDto emptyFilter = null;
        assertFalse(userPhoneFilter.isApplicable(emptyFilter));
    }

    @Test
    public void phonePatternIsNullTest() {
        assertFalse(userPhoneFilter.isApplicable(filter));
    }

    @Test
    public void phonePatternIsBlankTest() {
        filter.setPhonePattern("");

        assertFalse(userPhoneFilter.isApplicable(filter));
    }

    @Test
    public void applyWithMatchingPhonePatternShouldReturnFilteredUsersTest() {
        User user = mock(User.class);
        when(user.getPhone()).thenReturn("999");
        User user2 = mock(User.class);
        when(user2.getPhone()).thenReturn("000");

        filter.setPhonePattern("999");
        Stream<User> users = Stream.of(user, user2);

        List<User> filteredUsers = userPhoneFilter.apply(users, filter).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user));
        assertFalse(filteredUsers.contains(user2));
    }

    @Test
    public void applyWithNonMatchingPhonePatternShouldReturnEmptyStreamTest() {
        User user = mock(User.class);
        when(user.getPhone()).thenReturn("999");
        filter.setPhonePattern("000");
        Stream<User> users = Stream.of(user);

        List<User> filteredUsers = userPhoneFilter.apply(users, filter).toList();

        assertTrue(filteredUsers.isEmpty());
    }
}

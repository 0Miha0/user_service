package school.faang.user_service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserAboutFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAboutFilterTest {

    private UserAboutFilter userAboutFilter;
    private UserFilterDto filter;

    @BeforeEach
    void setUp() {
        filter = new UserFilterDto();
        userAboutFilter = new UserAboutFilter();
    }

    @Test
    public void isApplicableFilterTest() {
        filter.setAboutPattern("developer");

        assertTrue(userAboutFilter.isApplicable(filter));
    }

    @Test
    public void filterIsNullTest() {
        UserFilterDto emptyFilter = null;
        assertFalse(userAboutFilter.isApplicable(emptyFilter));
    }

    @Test
    public void aboutPatternIsNullTest() {
        assertFalse(userAboutFilter.isApplicable(filter));
    }

    @Test
    public void aboutPatternIsBlankTest() {
        filter.setAboutPattern("");

        assertFalse(userAboutFilter.isApplicable(filter));
    }

    @Test
    public void applyWithMatchingAboutPatternShouldReturnFilteredUsersTest() {
        User user = mock(User.class);
        when(user.getAboutMe()).thenReturn("I am a software developer");
        User user2 = mock(User.class);
        when(user2.getAboutMe()).thenReturn("I love music");
        User user3 = mock(User.class);
        when(user3.getAboutMe()).thenReturn("Experienced developer");

        filter.setAboutPattern("developer");
        Stream<User> users = Stream.of(user, user2, user3);

        List<User> filteredUsers = userAboutFilter.apply(users, filter).toList();

        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(user));
        assertTrue(filteredUsers.contains(user3));
        assertFalse(filteredUsers.contains(user2));
    }

    @Test
    public void applyWithNonMatchingAboutPatternShouldReturnEmptyStreamTest() {
        User user = mock(User.class);
        when(user.getAboutMe()).thenReturn("I am a software developer");
        filter.setAboutPattern("hello");
        Stream<User> users = Stream.of(user);

        List<User> filteredUsers = userAboutFilter.apply(users, filter).toList();

        assertTrue(filteredUsers.isEmpty());
    }
}
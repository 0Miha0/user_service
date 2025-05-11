package school.faang.user_service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserCityFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCityFilterTest {

    private UserCityFilter userCityFilter;
    private UserFilterDto filter;

    @BeforeEach
    void setUp() {
        filter = new UserFilterDto();
        userCityFilter = new UserCityFilter();
    }

    @Test
    public void isApplicableFilterTest() {
        filter.setCityPattern("New York");

        assertTrue(userCityFilter.isApplicable(filter));
    }

    @Test
    public void filterIsNullTest() {
        UserFilterDto emptyFilter = null;
        assertFalse(userCityFilter.isApplicable(emptyFilter));
    }

    @Test
    public void cityPatternIsNullTest() {
        assertFalse(userCityFilter.isApplicable(filter));
    }

    @Test
    public void cityPatternIsBlankTest() {
        filter.setAboutPattern("");

        assertFalse(userCityFilter.isApplicable(filter));
    }

    @Test
    public void applyWithMatchingCityPatternShouldReturnFilteredUsersTest() {
        User user = mock(User.class);
        when(user.getCity()).thenReturn("Chicago");
        User user2 = mock(User.class);
        when(user2.getCity()).thenReturn("some city");


        filter.setCityPattern("Chicago");
        Stream<User> users = Stream.of(user, user2);

        List<User> filteredUsers = userCityFilter.apply(users, filter).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user));
        assertFalse(filteredUsers.contains(user2));
    }

    @Test
    public void applyWithNonMatchingCityPatternShouldReturnEmptyStreamTest() {
        User user = mock(User.class);
        when(user.getCity()).thenReturn("Chicago");
        filter.setCityPattern("city");
        Stream<User> users = Stream.of(user);

        List<User> filteredUsers = userCityFilter.apply(users, filter).toList();

        assertTrue(filteredUsers.isEmpty());
    }
}
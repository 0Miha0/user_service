package school.faang.user_service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserCountryFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserCountryFilterTest {

    private UserCountryFilter userCountryFilter;
    private UserFilterDto filter;

    @BeforeEach
    void setUp() {
        filter = new UserFilterDto();
        userCountryFilter = new UserCountryFilter();
    }

    @Test
    public void isApplicableFilterTest() {
        filter.setCountryPattern("Paris");

        assertTrue(userCountryFilter.isApplicable(filter));
    }

    @Test
    public void filterIsNullTest() {
        UserFilterDto emptyFilter = null;
        assertFalse(userCountryFilter.isApplicable(emptyFilter));
    }

    @Test
    public void countryPatternIsNullTest() {
        assertFalse(userCountryFilter.isApplicable(filter));
    }

    @Test
    public void countryPatternIsBlank() {
        filter.setCountryPattern("");

        assertFalse(userCountryFilter.isApplicable(filter));
    }

    @Test
    public void applyWithMatchingCountryPatternShouldReturnFilteredUsersTest() {
        User user = mock(User.class);
        when(user.getCountry()).thenReturn(mock(Country.class));
        when(user.getCountry().getTitle()).thenReturn("Paris");

        User user2 = mock(User.class);
        when(user2.getCountry()).thenReturn(mock(Country.class));
        when(user2.getCountry().getTitle()).thenReturn("country");

        filter.setCountryPattern("Paris");
        Stream<User> users = Stream.of(user, user2);

        List<User> filteredUsers = userCountryFilter.apply(users, filter).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user));
        assertFalse(filteredUsers.contains(user2));
    }

    @Test
    public void applyWithNonMatchingCountryPatternShouldReturnEmptyStreamTest() {
        User user = mock(User.class);
        when(user.getCountry()).thenReturn(mock(Country.class));
        when(user.getCountry().getTitle()).thenReturn("Paris");
        filter.setCountryPattern("some country");
        Stream<User> users = Stream.of(user);

        List<User> filteredUsers = userCountryFilter.apply(users, filter).toList();

        assertTrue(filteredUsers.isEmpty());
    }
}
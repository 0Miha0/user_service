package school.faang.user_service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserPageSizeFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class UserPageSizeFilterTest {

    private UserPageSizeFilter userPageSizeFilter;
    private UserFilterDto filter;

    @BeforeEach
    void setUp() {
        filter = new UserFilterDto();
        userPageSizeFilter = new UserPageSizeFilter();
    }

    @Test
    public void isApplicableFilterTest() {
        filter.setPageSize(1);

        assertTrue(userPageSizeFilter.isApplicable(filter));
    }

    @Test
    public void filterIsNullTest() {
        UserFilterDto emptyFilter = null;
        assertFalse(userPageSizeFilter.isApplicable(emptyFilter));
    }

    @Test
    public void pageSizeLessThanZeroShouldReturnFalseTest() {
        filter.setPageSize(-1);

        assertFalse(userPageSizeFilter.isApplicable(filter));
    }

    @Test
    public void applyPageSizePatternShouldReturnFilteredUsersTest() {
        User user = mock(User.class);
        User user2 = mock(User.class);
        User user3 = mock(User.class);
        filter.setPageSize(2);
        Stream<User> users = Stream.of(user, user2, user3);

        List<User> filteredUsers = userPageSizeFilter.apply(users, filter).toList();

        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(user));
        assertTrue(filteredUsers.contains(user2));
        assertFalse(filteredUsers.contains(user3));
    }
}
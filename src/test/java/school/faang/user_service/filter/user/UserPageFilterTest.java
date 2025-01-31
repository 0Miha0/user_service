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

@ExtendWith(MockitoExtension.class)
public class UserPageFilterTest {

    private UserPageFilter userPageFilter;
    private UserFilterDto filter;

    @BeforeEach
    void setUp() {
        filter = new UserFilterDto();
        userPageFilter = new UserPageFilter();
    }

    @Test
    public void isApplicableFilterTest() {
        filter.setPageSize(1);
        filter.setPage(1);

        assertTrue(userPageFilter.isApplicable(filter));
    }

    @Test
    public void filterIsNullTest() {
        UserFilterDto emptyFilter = null;
        assertFalse(userPageFilter.isApplicable(emptyFilter));
    }

    @Test
    public void pageSizeLessThanZeroShouldReturnFalseTest() {
        filter.setPageSize(-1);

        assertFalse(userPageFilter.isApplicable(filter));
    }

    @Test
    public void pageLessThanZeroShouldReturnFalseTest() {
        filter.setPage(-1);

        assertFalse(userPageFilter.isApplicable(filter));
    }

    @Test
    public void applyPagePatternShouldReturnFilteredUsersTest() {
        User user = mock(User.class);
        User user2 = mock(User.class);
        User user3 = mock(User.class);
        filter.setPageSize(1);
        filter.setPage(2);
        Stream<User> users = Stream.of(user, user2, user3);

        List<User> filteredUsers = userPageFilter.apply(users, filter).toList();

        assertEquals(1, filteredUsers.size());
        assertFalse(filteredUsers.contains(user));
        assertTrue(filteredUsers.contains(user2));
        assertFalse(filteredUsers.contains(user3));
    }
}

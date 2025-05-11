package school.faang.user_service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.filter.user.UserContactFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserContactFilterTest {

    private UserContactFilter userContactFilter;
    private UserFilterDto filter;

    @BeforeEach
    void setUp() {
        filter = new UserFilterDto();
        userContactFilter = new UserContactFilter();
    }

    @Test
    public void isApplicableFilterTest() {
        filter.setContactPattern("contact");

        assertTrue(userContactFilter.isApplicable(filter));
    }

    @Test
    public void filterIsNullTest() {
        UserFilterDto emptyFilter = null;
        assertFalse(userContactFilter.isApplicable(emptyFilter));
    }

    @Test
    public void contactPatternIsNullTest() {
        assertFalse(userContactFilter.isApplicable(filter));
    }

    @Test
    public void contactPatternIsBlank() {
        filter.setAboutPattern("");

        assertFalse(userContactFilter.isApplicable(filter));
    }

    @Test
    public void applyWithMatchingContactPatternShouldReturnFilteredUsersTest() {
        List<Contact> contacts = new ArrayList<>(List.of(mock(Contact.class)));
        List<Contact> contacts2 = new ArrayList<>(List.of(mock(Contact.class)));

        User user = mock(User.class);
        when(user.getContacts()).thenReturn(contacts);
        when(user.getContacts().get(0).getContact()).thenReturn("contact");

        User user2 = mock(User.class);
        when(user2.getContacts()).thenReturn(contacts2);
        when(user2.getContacts().get(0).getContact()).thenReturn("another");

        filter.setContactPattern("contact");
        Stream<User> users = Stream.of(user, user2);

        List<User> filteredUsers = userContactFilter.apply(users, filter).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user));
        assertFalse(filteredUsers.contains(user2));
    }

    @Test
    public void applyWithNonMatchingContactPatternShouldReturnEmptyStreamTest() {
        List<Contact> contacts = new ArrayList<>(List.of(mock(Contact.class)));
        User user = mock(User.class);
        when(user.getContacts()).thenReturn(contacts);
        when(user.getContacts().get(0).getContact()).thenReturn("contact");

        filter.setContactPattern("another");
        Stream<User> users = Stream.of(user);

        List<User> filteredUsers = userContactFilter.apply(users, filter).toList();

        assertTrue(filteredUsers.isEmpty());
    }
}
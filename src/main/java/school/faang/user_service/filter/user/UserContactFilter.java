package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserContactFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter != null &&
                filter.getContactPattern() != null &&
                !filter.getContactPattern().isEmpty();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        return users.filter(user -> user.getContacts().stream().
                anyMatch(contact -> contact.getContact().contains(filter.getContactPattern())));
    }
}

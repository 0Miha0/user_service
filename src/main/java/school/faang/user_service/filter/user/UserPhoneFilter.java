package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserPhoneFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter != null &&
                filter.getPhonePattern() != null &&
                !filter.getPhonePattern().isEmpty();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        return users.filter(user -> user.getPhone().contains(filter.getPhonePattern()));
    }
}

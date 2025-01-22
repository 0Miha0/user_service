package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserPageSizeFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter != null && filter.getPageSize() > 0;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        return users.limit(filter.getPageSize());
    }
}

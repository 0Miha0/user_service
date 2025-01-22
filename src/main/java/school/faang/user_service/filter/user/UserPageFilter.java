package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserPageFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter != null &&
                filter.getPage() > 0 &&
                filter.getPageSize() > 0;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        int pageNumber = filter.getPage();
        int pageSize = filter.getPageSize();
        long usersToSkip = ((long) alignPageNumber(pageNumber) * pageSize);

        return users
                .skip(usersToSkip)
                .limit(pageSize);
    }

    private int alignPageNumber(int pageNumb) {
        return pageNumb - 1;
    }
}

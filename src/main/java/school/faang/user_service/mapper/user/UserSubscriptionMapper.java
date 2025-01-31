package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.user_subscription.UserSubscriptionDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSubscriptionMapper {

    UserSubscriptionDto toDto(User user);

    User toEntity(UserSubscriptionDto dto);

    List<UserSubscriptionDto> toDtoList(List<User> users);
}

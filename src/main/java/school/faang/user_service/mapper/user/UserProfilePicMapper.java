package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.user.UserProfilePicDto;
import school.faang.user_service.entity.UserProfilePic;

@Mapper(componentModel = "spring")
public interface UserProfilePicMapper {

    UserProfilePicDto toDto(UserProfilePic userProfilePic);
}

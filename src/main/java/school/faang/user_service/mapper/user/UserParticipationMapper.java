package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserParticipationDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserParticipationMapper {
    User toEntity(UserParticipationDto dto);

    UserParticipationDto toDto(User user);
}

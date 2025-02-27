package school.faang.user_service.mapper.goal;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    Goal toEntity(GoalDto dto);

    GoalDto toDto(Goal entity);

    List<Goal> toEntityLst(List<GoalDto> dto);

    List<GoalDto> toDtoList(List<Goal> entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(GoalDto dto, @MappingTarget Goal entity);
}

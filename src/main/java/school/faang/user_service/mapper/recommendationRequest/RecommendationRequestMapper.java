package school.faang.user_service.mapper.recommendationRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {

    @Mapping(source = "skills", target = "skills", qualifiedByName = "mapSkillRequestsToIds")
    @Mapping(source = "requester", target = "requester", qualifiedByName = "mapUserToId")
    @Mapping(source = "receiver", target = "receiver", qualifiedByName = "mapUserToId")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);

    List<RecommendationRequestDto> toDtoList(List<RecommendationRequest> recommendationRequestDtos);

    @Named("mapSkillRequestsToIds")
    default List<Long> mapSkillRequestsToIds(List<SkillRequest> skillRequests) {
        return skillRequests.stream()
                .map(SkillRequest::getId)
                .toList();
    }

    @Named("mapUserToId")
    default Long mapUserToId(User userId){
        return userId.getId();
    }
}

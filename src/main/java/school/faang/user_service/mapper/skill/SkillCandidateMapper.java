package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillCandidateMapper {

    default List<SkillCandidateDto> toSkillCandidateDtoList(List<SkillDto> skills) {
        return skills.stream()
                .collect(Collectors.groupingBy(skillDto -> skillDto, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new SkillCandidateDto(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(skill -> skill.getSkillDto().getId()))
                .toList();
    }
}

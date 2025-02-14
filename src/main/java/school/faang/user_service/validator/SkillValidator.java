package school.faang.user_service.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.customexception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

@Component
@AllArgsConstructor
public class SkillValidator {

    private final SkillRepository skillRepository;

    public void validateTitle(SkillDto skillDto) {
        if (skillDto.getTitle() == null || skillDto.getTitle().isBlank()) {
            throw new DataValidationException("Skill title cannot be null or blank");
        }

        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Skill with the same title already exists");
        }
    }

    public Skill skillAlreadyExists(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Skill not found with ID " + skillId + " in the system"));
    }
}

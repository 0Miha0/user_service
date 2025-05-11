package school.faang.user_service.validator.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillValidator skillValidator;


    @Test
    public void createSkillWithEmptyTitleTest() {
        SkillDto skillDto = new SkillDto(1L, null);

        assertThrows(DataValidationException.class, () -> skillValidator.validateTitle(skillDto));
    }

    @Test
    public void createSkillWithBlankTitleTest() {
        SkillDto skillDto = new SkillDto(1L, "");

        assertThrows(DataValidationException.class, () -> skillValidator.validateTitle(skillDto));
    }

    @Test
    public void createSkillWhichAlreadyExistsTest() {
        SkillDto skillDto = new SkillDto(1L, "Skill");
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillValidator.validateTitle(skillDto));
    }

    @Test
    public void skillNoExistTest() {
        long skillId = 1L;

        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> skillValidator.skillAlreadyExists(skillId));
    }
}

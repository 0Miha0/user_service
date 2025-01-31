package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.skill.SkillDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SkillControllerIT {

    @Autowired
    private SkillController skillController;

    @Test
    public void createSkill() {
        SkillDto skillDto = skillController.createSkill(SkillDto.builder().title("Javava vava va ava1").build()).getBody();

        assertEquals("Javava vava va ava1", skillDto.getTitle());
    }
}

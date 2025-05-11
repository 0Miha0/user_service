package school.faang.user_service.controller.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    @Captor
    ArgumentCaptor<SkillDto> captorSkillDto;

    private long userId;
    private long recommenderId;
    private long skillId;
    private SkillDto firstSkill;
    private SkillDto secondSkill;
    private SkillCandidateDto firstCandidateSkill;
    private SkillCandidateDto secondCandidateSkill;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        recommenderId = 2L;
        skillId = 2L;
        firstSkill = new SkillDto(1L, "First skill");
        secondSkill = new SkillDto(2L, "Second skill");
        firstCandidateSkill = new SkillCandidateDto(firstSkill, 1);
        secondCandidateSkill = new SkillCandidateDto(secondSkill, 1);
    }

    @Test
    public void createSkillTest() {
        when(skillService.create(firstSkill)).thenReturn(firstSkill);

        skillController.create(firstSkill);

        verify(skillService, times(1)).create(captorSkillDto.capture());
        SkillDto createSkill = captorSkillDto.getValue();
        assertEquals(firstSkill.getId(), createSkill.getId());
        assertEquals(firstSkill.getTitle(), createSkill.getTitle());
    }

    @Test
    public void getUserSkillsTest() {
        when(skillService.getUserSkills(userId)).thenReturn(List.of(firstSkill, secondSkill));

        List<SkillDto> skills = skillController.getUserSkills(userId);

        verify(skillService, times(1)).getUserSkills(userId);
        assertEquals(2, skills.size());
        assertEquals(firstSkill.getId(), skills.get(0).getId());
        assertEquals(firstSkill.getTitle(), skills.get(0).getTitle());
        assertEquals(secondSkill.getId(), skills.get(1).getId());
        assertEquals(secondSkill.getTitle(), skills.get(1).getTitle());
    }

    @Test
    public void getOfferedSkillsTest() {
        when(skillService.getOfferedSkills(userId)).thenReturn(List.of(firstCandidateSkill, secondCandidateSkill));

        List<SkillCandidateDto> skills = skillController.getOfferedSkills(userId);

        verify(skillService, times(1)).getOfferedSkills(userId);
        assertEquals(2, skills.size());
        assertEquals(firstCandidateSkill.getSkill(), skills.get(0).getSkill());
        assertEquals(firstCandidateSkill.getOffersAmount(), skills.get(0).getOffersAmount());
        assertEquals(secondCandidateSkill.getSkill(), skills.get(1).getSkill());
        assertEquals(secondCandidateSkill.getOffersAmount(), skills.get(1).getOffersAmount());
    }

    @Test
    public void acquireSkillFromOffersTest() {
        when(skillService.acquireSkillFromOffers(skillId, userId, recommenderId)).thenReturn(firstSkill);

        SkillDto skill = skillController.acquireSkillFromOffers(skillId, userId, recommenderId);

        verify(skillService, times(1)).acquireSkillFromOffers(skillId, userId, recommenderId);
        assertEquals(firstSkill.getId(), skill.getId());
        assertEquals(firstSkill.getTitle(), skill.getTitle());
    }
}
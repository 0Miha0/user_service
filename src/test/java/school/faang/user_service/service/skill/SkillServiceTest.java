package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//@ExtendWith(MockitoExtension.class)
//public class SkillServiceTest {
//
//    @Mock
//    private SkillRepository skillRepository;
//
//    @Mock
//    private SkillOfferRepository skillOfferRepository;
//
//    @Spy
//    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);
//
//    @Spy
//    private SkillCandidateMapper skillCandidateMapper = Mappers.getMapper(SkillCandidateMapper.class);
//
//    @Mock
//    private SkillValidator skillValidator;
//
//    @Mock
//    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
//
//    @InjectMocks
//    private SkillService skillService;
//
//    private long userId;
//    private long recommenderId;
//    private long skillId;
//    private User user;
//    private SkillDto skillDto;
//    private Skill firstSkill;
//    private Skill secondSkill;
//
//    @BeforeEach
//    public void setUp() {
//        userId = 1L;
//        recommenderId = 2L;
//        user = new User();
//        user.setId(userId);
//
//        skillId = 1L;
//        skillDto = new SkillDto(skillId, "New skill");
//
//        firstSkill = new Skill();
//        firstSkill.setId(1L);
//        firstSkill.setTitle("First skill");
//
//        secondSkill = new Skill();
//        secondSkill.setId(2L);
//        secondSkill.setTitle("Second skill");
//    }
//
//    @Test
//    public void createSkillTest() {
//        Skill skill = skillMapper.toEntity(skillDto);
//        when(skillRepository.save(skill)).thenReturn(skill);
//
//        SkillDto createSkillDto = skillService.createSkill(skillDto);
//
//        verify(skillRepository, times(1)).save(skill);
//        assertEquals(skillDto.getId(), createSkillDto.getId());
//        assertEquals(skillDto.getTitle(), createSkillDto.getTitle());
//    }
//
//    @Test
//    public void getUserSkillsTest() {
//        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(firstSkill, secondSkill));
//
//        List<SkillDto> skills = skillService.getUserSkills(userId);
//
//        verify(skillRepository, times(1)).findAllByUserId(userId);
//        assertEquals(2, skills.size());
//        assertEquals(firstSkill.getId(), skills.get(0).getId());
//        assertEquals(firstSkill.getTitle(), skills.get(0).getTitle());
//        assertEquals(secondSkill.getId(), skills.get(1).getId());
//        assertEquals(secondSkill.getTitle(), skills.get(1).getTitle());
//    }
//
//    @Test
//    public void getOfferedSkillsTest() {
//        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(firstSkill, secondSkill));
//
//        List<SkillCandidateDto> skills = skillService.getOfferedSkill(userId);
//
//        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
//        assertEquals(2, skills.size());
//        assertEquals(firstSkill.getTitle(), skills.get(0).getSkillDto().getTitle());
//        assertEquals(1, skills.get(0).getOffersAmount());
//        assertEquals(secondSkill.getTitle(), skills.get(1).getSkillDto().getTitle());
//        assertEquals(1, skills.get(1).getOffersAmount());
//    }
//
//    @Test
//    public void acquireSkillIfSkillAlreadyBeenAddedTest() {
//        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(firstSkill));
//
//        SkillDto skillDto = skillService.acquireSkillFromOffers(skillId, userId);
//
//        assertEquals(firstSkill.getId(), skillDto.getId());
//        assertEquals(firstSkill.getTitle(), skillDto.getTitle());
//        verify(skillOfferRepository, times(0)).findAllOffersOfSkill(skillId, userId);
//    }
//
//    @Test
//    public void acquireSkillWhenLessThanThreeGuarantorsTest() {
//        when(skillValidator.skillAlreadyExists(firstSkill.getId())).thenReturn(firstSkill);
//        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
//        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId))
//                .thenReturn(List.of(new SkillOffer(), new SkillOffer()));
//
//        SkillDto skillDto = skillService.acquireSkillFromOffers(skillId, userId);
//
//        assertEquals(firstSkill.getId(), skillDto.getId());
//        assertEquals(firstSkill.getTitle(), skillDto.getTitle());
//        verify(skillRepository, times(0)).assignSkillToUser(skillId, userId);
//    }
//
//    @Test
//    public void acquireSkillFromOffersSkillTest() {
//        Recommendation recommendation = Recommendation.builder()
//                .author(new User())
//                .receiver(new User())
//                .build();
//        SkillOffer firstSkillOffer = SkillOffer.builder()
//                .recommendation(recommendation)
//                .skill(firstSkill)
//                .build();
//        SkillOffer secondSkillOffer = SkillOffer.builder()
//                .recommendation(recommendation)
//                .skill(firstSkill)
//                .build();
//        SkillOffer thirdSkillOffer = SkillOffer.builder()
//                .recommendation(recommendation)
//                .skill(firstSkill)
//                .build();
//        List<SkillOffer> skillOffers = List.of(firstSkillOffer, secondSkillOffer, thirdSkillOffer);
//
//        when(skillValidator.skillAlreadyExists(firstSkill.getId())).thenReturn(firstSkill);
//        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
//        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);
//
//        SkillDto skillDto = skillService.acquireSkillFromOffers(skillId, userId);
//
//        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
//        verify(userSkillGuaranteeRepository, times(skillOffers.size())).save(any());
//        assertEquals(firstSkill.getId(), skillDto.getId());
//        assertEquals(firstSkill.getTitle(), skillDto.getTitle());
//    }
//}

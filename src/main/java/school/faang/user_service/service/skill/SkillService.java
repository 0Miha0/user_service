package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SkillService {

    private static final int MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper;
    private final SkillValidator skillValidator;
    private final SkillCandidateMapper skillCandidateMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public SkillDto createSkill(SkillDto skillDto) {
        log.info("Creating skill {}", skillDto.getTitle());
        skillValidator.validateTitle(skillDto);
        Skill skill = skillMapper.toEntity(skillDto);
        skill = skillRepository.save(skill);
        log.info("Created skill {}", skill.getTitle());
        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        log.info("Getting skills for user {}", userId);
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        log.info("From user {} found {} skills", userId, skills.size());
        return skillMapper.toDtoList(skills);
    }

    public List<SkillCandidateDto> getOfferedSkill(long userId) {
        log.info("Getting offered skills for user {}", userId);

        List<SkillDto> skills = skillMapper.toDtoList(skillRepository.findSkillsOfferedToUser(userId));

        log.info("From user {} found {} offered skills", userId, skills.size());
        return skillCandidateMapper.toSkillCandidateDtoList(skills);
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        log.info("Acquiring skill {} from offers for user {}", skillId, userId);
        Skill skill = skillValidator.skillAlreadyExists(skillId);

        Optional<Skill> skillOptional = skillRepository.findUserSkill(skillId, userId);
        if (skillOptional.isPresent()) {
            log.info("User {} already has skill {}", userId, skillId);
            return skillMapper.toDto(skillOptional.get());
        }

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (skillOffers.size() >= MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);
            log.info("User {} acquired skill {}", userId, skillId);

            for (SkillOffer skillOffer : skillOffers) {
                UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                        .user(skillOffer.getRecommendation().getReceiver())
                        .guarantor(skillOffer.getRecommendation().getAuthor())
                        .skill(skillOffer.getSkill())
                        .build();
                userSkillGuaranteeRepository.save(guarantee);
            }
        }
        log.info("No more offers for skill {} from user {}", skillId, userId);
        return skillMapper.toDto(skill);
    }
}

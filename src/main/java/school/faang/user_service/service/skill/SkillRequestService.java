package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillRequestService {

    private final SkillRequestRepository skillRequestRepository;
    private final SkillRepository skillRepository;

    public List<SkillRequest> findAllById(List<Long> skillIds) {
        log.info("Finding skill requests for skill IDs: {}", skillIds);
        return skillRequestRepository.findAllById(skillIds);
    }

    public void saveSkillRequests(RecommendationRequest recommendationRequestEntity, List<Long> skillIds) {

        List<SkillRequest> skillRequests = skillIds.stream()
                .map(skillId -> new SkillRequest(0, recommendationRequestEntity, skillRepository.findById(skillId)
                        .orElseThrow(() -> {
                            log.error("Skill with ID {} not found", skillId);
                            return new DataValidationException("Skill with ID " + skillId + " not found" + skillId);
                        })))
                .toList();
        skillRequestRepository.saveAll(skillRequests);
        log.info("Skill requests successfully saved for Recommendation Request ID: {}", recommendationRequestEntity.getId());
    }
}

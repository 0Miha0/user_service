package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestDto;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestRejectionDto;
import school.faang.user_service.service.recommendationRequest.RecommendationRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/recommendation-requests")
@RequiredArgsConstructor
public class RecommendationRequestController {

    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/recommendations")
    public ResponseEntity<RecommendationRequestDto> requestRecommendation(@Valid @RequestBody RecommendationRequestDto recommendationRequest) {
        return ResponseEntity.ok(recommendationRequestService.create(recommendationRequest));
    }

    @PostMapping("/filter")
    public ResponseEntity<List<RecommendationRequestDto>> getRecommendationRequests(@RequestBody RecommendationRequestFilterDto filter) {
        return ResponseEntity.ok(recommendationRequestService.getRequest(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendationRequestDto> getRecommendationRequest(@PathVariable Long id) {
        return ResponseEntity.ok(recommendationRequestService.getRequest(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<RecommendationRequestDto> rejectRequest(@PathVariable Long id, @Valid RecommendationRequestRejectionDto recommendationRequestRejectionDto) {
        return ResponseEntity.ok(recommendationRequestService.rejectRequest(id, recommendationRequestRejectionDto));
    }
}

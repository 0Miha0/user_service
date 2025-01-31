package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationController {
    public final RecommendationService recommendationService;

    @GetMapping("/receivers/{receiverId}")
    public ResponseEntity<List<RecommendationDto>> getAllUserRecommendations(@PathVariable Long receiverId) {
        return new ResponseEntity<>(recommendationService.getAllUserRecommendations(receiverId), HttpStatus.OK);
    }

    @GetMapping("/authors/{authorId}")
    public ResponseEntity<List<RecommendationDto>> getAllGivenRecommendations(@PathVariable Long authorId) {
        return new ResponseEntity<>(recommendationService.getAllGivenRecommendations(authorId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RecommendationDto> giveRecommendation(@RequestBody @Valid RecommendationDto recommendation) {
        return new ResponseEntity<>(recommendationService.create(recommendation), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<RecommendationDto> updateRecommendation(@RequestBody @Valid RecommendationDto updated) {
        return new ResponseEntity<>(recommendationService.update(updated), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable Long id) {
        recommendationService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

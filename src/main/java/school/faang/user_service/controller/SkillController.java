package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Tag(name = "API for managing skills",
    description = "API endpoints for managing skills")
@RestController
@RequiredArgsConstructor
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;

    @Operation(summary = "Create a new skill")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Skill created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid skill data")
    })
    @PostMapping
    public ResponseEntity<SkillDto> createSkill(@RequestBody SkillDto dto){
        return ResponseEntity.ok(skillService.createSkill(dto));
    }

    @Operation(summary = "Get all user skills")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User skills retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<SkillDto>> getUserSkills(@PathVariable Long userId){
        return ResponseEntity.ok(skillService.getUserSkills(userId));
    }

    @Operation(summary = "Get offered skill")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Offered skill retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}/offers")
    public ResponseEntity<List<SkillCandidateDto>> getOfferedSkill(@PathVariable Long userId){
        return ResponseEntity.ok(skillService.getOfferedSkill(userId));
    }

    @Operation(summary = "Acquire a skill from the offers list for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Skill acquired successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid skill or user ID")
    })
    @PutMapping("/{userId}/offers/{skillId}")
    public SkillDto acquireSkillFromOffers(
            @PathVariable long skillId,
            @PathVariable long userId
    ) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}

package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.services.InstructorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors/{instructorId}/profile")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class InstructorProfileController {

    @Autowired
    private InstructorProfileService instructorProfileService;

    // ==================== EDUCATION ====================

    @GetMapping("/educations")
    public ResponseEntity<List<EducationDTO>> getEducations(@PathVariable Long instructorId) {
        return ResponseEntity.ok(instructorProfileService.getEducations(instructorId));
    }

    @GetMapping("/educations/{educationId}")
    public ResponseEntity<EducationDTO> getEducationById(@PathVariable Long instructorId, @PathVariable Long educationId) {
        return ResponseEntity.ok(instructorProfileService.getEducationById(instructorId, educationId));
    }

    @PostMapping("/educations")
    public ResponseEntity<EducationDTO> addEducation(@PathVariable Long instructorId, @RequestBody EducationDTO educationDTO) {
        return ResponseEntity.ok(instructorProfileService.addEducation(instructorId, educationDTO));
    }

    @PutMapping("/educations/{educationId}")
    public ResponseEntity<EducationDTO> updateEducation(
            @PathVariable Long instructorId,
            @PathVariable Long educationId,
            @RequestBody EducationDTO educationDTO) {
        return ResponseEntity.ok(instructorProfileService.updateEducation(instructorId, educationId, educationDTO));
    }

    @DeleteMapping("/educations/{educationId}")
    public ResponseEntity<Void> deleteEducation(@PathVariable Long instructorId, @PathVariable Long educationId) {
        instructorProfileService.deleteEducation(instructorId, educationId);
        return ResponseEntity.noContent().build();
    }

    // ==================== EXPERIENCE ====================

    @GetMapping("/experiences")
    public ResponseEntity<List<ExperienceDTO>> getExperiences(@PathVariable Long instructorId) {
        return ResponseEntity.ok(instructorProfileService.getExperiences(instructorId));
    }

    @GetMapping("/experiences/{experienceId}")
    public ResponseEntity<ExperienceDTO> getExperienceById(@PathVariable Long instructorId, @PathVariable Long experienceId) {
        return ResponseEntity.ok(instructorProfileService.getExperienceById(instructorId, experienceId));
    }

    @PostMapping("/experiences")
    public ResponseEntity<ExperienceDTO> addExperience(@PathVariable Long instructorId, @RequestBody ExperienceDTO experienceDTO) {
        return ResponseEntity.ok(instructorProfileService.addExperience(instructorId, experienceDTO));
    }

    @PutMapping("/experiences/{experienceId}")
    public ResponseEntity<ExperienceDTO> updateExperience(
            @PathVariable Long instructorId,
            @PathVariable Long experienceId,
            @RequestBody ExperienceDTO experienceDTO) {
        return ResponseEntity.ok(instructorProfileService.updateExperience(instructorId, experienceId, experienceDTO));
    }

    @DeleteMapping("/experiences/{experienceId}")
    public ResponseEntity<Void> deleteExperience(@PathVariable Long instructorId, @PathVariable Long experienceId) {
        instructorProfileService.deleteExperience(instructorId, experienceId);
        return ResponseEntity.noContent().build();
    }

    // ==================== SKILLS ====================

    @GetMapping("/skills")
    public ResponseEntity<List<SkillDTO>> getSkills(@PathVariable Long instructorId) {
        return ResponseEntity.ok(instructorProfileService.getSkills(instructorId));
    }

    @GetMapping("/skills/{skillId}")
    public ResponseEntity<SkillDTO> getSkillById(@PathVariable Long instructorId, @PathVariable Long skillId) {
        return ResponseEntity.ok(instructorProfileService.getSkillById(instructorId, skillId));
    }

    @PostMapping("/skills")
    public ResponseEntity<SkillDTO> addSkill(@PathVariable Long instructorId, @RequestBody SkillDTO skillDTO) {
        return ResponseEntity.ok(instructorProfileService.addSkill(instructorId, skillDTO));
    }

    @PutMapping("/skills/{skillId}")
    public ResponseEntity<SkillDTO> updateSkill(
            @PathVariable Long instructorId,
            @PathVariable Long skillId,
            @RequestBody SkillDTO skillDTO) {
        return ResponseEntity.ok(instructorProfileService.updateSkill(instructorId, skillId, skillDTO));
    }

    @DeleteMapping("/skills/{skillId}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long instructorId, @PathVariable Long skillId) {
        instructorProfileService.deleteSkill(instructorId, skillId);
        return ResponseEntity.noContent().build();
    }

    // ==================== SOCIAL ====================

    @GetMapping("/socials")
    public ResponseEntity<SocialDTO> getSocial(@PathVariable Long instructorId) {
        return ResponseEntity.ok(instructorProfileService.getSocial(instructorId));
    }

    @PutMapping("/socials")
    public ResponseEntity<SocialDTO> updateSocial(@PathVariable Long instructorId, @RequestBody SocialDTO socialDTO) {
        return ResponseEntity.ok(instructorProfileService.updateSocial(instructorId, socialDTO));
    }
}

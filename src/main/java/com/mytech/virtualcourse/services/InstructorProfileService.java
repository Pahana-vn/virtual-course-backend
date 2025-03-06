package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.*;
import com.mytech.virtualcourse.mappers.InstructorProfileMapper;
import com.mytech.virtualcourse.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstructorProfileService {

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private SocialRepository socialRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private InstructorProfileMapper mapper;

    // ==================== EDUCATION ====================

    public List<EducationDTO> getEducations(Long instructorId) {
        return mapper.toEducationDTOList(educationRepository.findByInstructorId(instructorId));
    }

    public EducationDTO getEducationById(Long instructorId, Long educationId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new RuntimeException("Education not found"));

        if (!education.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Unauthorized access to education record");
        }

        return mapper.toEducationDTO(education);
    }

    public EducationDTO addEducation(Long instructorId, EducationDTO educationDTO) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        Education education = mapper.toEducationEntity(educationDTO);
        education.setInstructor(instructor);
        educationRepository.save(education);
        return mapper.toEducationDTO(education);
    }

    public EducationDTO updateEducation(Long instructorId, Long educationId, EducationDTO educationDTO) {
        Education existingEducation = educationRepository.findById(educationId)
                .orElseThrow(() -> new RuntimeException("Education not found"));

        if (!existingEducation.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Unauthorized access to update education record");
        }

        existingEducation.setDegree(educationDTO.getDegree());
        existingEducation.setUniversity(educationDTO.getUniversity());
        existingEducation.setStartYear(educationDTO.getStartYear());
        existingEducation.setEndYear(educationDTO.getEndYear());
        existingEducation.setDescription(educationDTO.getDescription());

        educationRepository.save(existingEducation);
        return mapper.toEducationDTO(existingEducation);
    }

    public void deleteEducation(Long instructorId, Long educationId) {
        Education existingEducation = educationRepository.findById(educationId)
                .orElseThrow(() -> new RuntimeException("Education not found"));

        if (!existingEducation.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Unauthorized access to delete education record");
        }

        educationRepository.delete(existingEducation);
    }

    // ==================== EXPERIENCE ====================

    public List<ExperienceDTO> getExperiences(Long instructorId) {
        return mapper.toExperienceDTOList(experienceRepository.findByInstructorId(instructorId));
    }

    public ExperienceDTO getExperienceById(Long instructorId, Long experienceId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException("Experience not found"));

        if (!experience.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Unauthorized access to experience record");
        }

        return mapper.toExperienceDTO(experience);
    }

    public ExperienceDTO addExperience(Long instructorId, ExperienceDTO experienceDTO) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        Experience experience = mapper.toExperienceEntity(experienceDTO);
        experience.setInstructor(instructor); // Gán instructor vào experience
        experienceRepository.save(experience);
        return mapper.toExperienceDTO(experience);
    }

    public ExperienceDTO updateExperience(Long instructorId, Long experienceId, ExperienceDTO experienceDTO) {
        Experience existingExperience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException("Experience not found"));

        if (!existingExperience.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Unauthorized access to update experience record");
        }

        existingExperience.setPosition(experienceDTO.getPosition());
        existingExperience.setCompany(experienceDTO.getCompany());
        existingExperience.setStartYear(experienceDTO.getStartYear());
        existingExperience.setEndYear(experienceDTO.getEndYear());
        existingExperience.setDescription(experienceDTO.getDescription());

        experienceRepository.save(existingExperience);
        return mapper.toExperienceDTO(existingExperience);
    }

    public void deleteExperience(Long instructorId, Long experienceId) {
        Experience existingExperience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException("Experience not found"));

        if (!existingExperience.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Unauthorized access to delete experience record");
        }

        experienceRepository.delete(existingExperience);
    }

    // ==================== SKILLS ====================

    public List<SkillDTO> getSkills(Long instructorId) {
        return mapper.toSkillDTOList(skillRepository.findByInstructorId(instructorId));
    }

    public SkillDTO getSkillById(Long instructorId, Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        if (!skill.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Unauthorized access to skill record");
        }

        return mapper.toSkillDTO(skill);
    }

    public SkillDTO addSkill(Long instructorId, SkillDTO skillDTO) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        Skill skill = mapper.toSkillEntity(skillDTO);
        skill.setInstructor(instructor); // Gán instructor vào skill
        skillRepository.save(skill);
        return mapper.toSkillDTO(skill);
    }

    public SkillDTO updateSkill(Long instructorId, Long skillId, SkillDTO skillDTO) {
        Skill existingSkill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        if (!existingSkill.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Unauthorized access to update skill record");
        }

        existingSkill.setSkillName(skillDTO.getSkillName());

        skillRepository.save(existingSkill);
        return mapper.toSkillDTO(existingSkill);
    }

    public void deleteSkill(Long instructorId, Long skillId) {
        Skill existingSkill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        if (!existingSkill.getInstructor().getId().equals(instructorId)) {
            throw new RuntimeException("Unauthorized access to delete skill record");
        }

        skillRepository.delete(existingSkill);
    }

    // ==================== SOCIAL ====================

    public SocialDTO getSocial(Long instructorId) {
        return socialRepository.findByInstructorId(instructorId)
                .map(mapper::toSocialDTO)
                .orElseThrow(() -> new RuntimeException("Social profile not found"));
    }

    public SocialDTO updateSocial(Long instructorId, SocialDTO socialDTO) {
        Optional<Social> existingSocial = socialRepository.findByInstructorId(instructorId);

        Social social;
        if (existingSocial.isPresent()) {
            social = existingSocial.get();
            social.setFacebookUrl(socialDTO.getFacebookUrl());
            social.setGoogleUrl(socialDTO.getGoogleUrl());
            social.setInstagramUrl(socialDTO.getInstagramUrl());
            social.setLinkedinUrl(socialDTO.getLinkedinUrl());
        } else {
            social = mapper.toSocialEntity(socialDTO);
        }

        socialRepository.save(social);
        return mapper.toSocialDTO(social);
    }
}

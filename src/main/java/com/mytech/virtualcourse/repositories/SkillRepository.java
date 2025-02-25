package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByInstructorId(Long instructorId);
}

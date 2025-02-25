package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findByInstructorId(Long instructorId);
}

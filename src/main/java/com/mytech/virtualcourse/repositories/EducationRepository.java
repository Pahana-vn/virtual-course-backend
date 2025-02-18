package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByInstructorId(Long instructorId);
}

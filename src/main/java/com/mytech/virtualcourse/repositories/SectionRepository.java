package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepository  extends JpaRepository<Section, Long> {
    @Query("SELECT COUNT(s) FROM Section s JOIN s.course c WHERE c.instructor.id = :instructorId")
    int countByInstructorId(@Param("instructorId") Long instructorId);
}

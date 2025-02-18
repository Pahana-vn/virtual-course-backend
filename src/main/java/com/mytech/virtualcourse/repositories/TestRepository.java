package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByCourseId(Long courseId);

    @Query("SELECT t FROM Test t WHERE t.course.id = :courseId AND t.isFinalTest = true")
    Optional<Test> findFinalTestByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT t FROM Test t WHERE t.instructor.id = :instructorId AND t.course.id = :courseId")
    List<Test> findByInstructorIdAndCourseId(@Param("instructorId") Long instructorId, @Param("courseId") Long courseId);
}

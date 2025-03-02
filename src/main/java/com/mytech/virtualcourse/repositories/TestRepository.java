package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Test;
import com.mytech.virtualcourse.enums.StatusTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT t FROM Test t JOIN t.course c WHERE c.instructor.id = :instructorId")
    Page<Test> findByInstructorId(@Param("instructorId") Long instructorId, Pageable pageable);

    @Query("SELECT t FROM Test t JOIN t.course c WHERE c.instructor.id = :instructorId AND t.statusTest = :status")
    Page<Test> findByInstructorIdAndStatus(
            @Param("instructorId") Long instructorId,
            @Param("status") StatusTest status,
            Pageable pageable);
}

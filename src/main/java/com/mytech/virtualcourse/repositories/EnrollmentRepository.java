// src/main/java/com/mytech/virtualcourse/repositories/EnrollmentRepository.java

package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Enrollment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByCourseId(Long courseId);
}

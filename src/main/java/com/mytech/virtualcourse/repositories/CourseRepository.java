package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.enums.ECourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByInstructor(Instructor instructor);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.instructor.id = :instructorId")
    int countByInstructorId(@Param("instructorId") Long instructorId);

    List<Course> findByInstructorAndStatus(Instructor instructor, ECourseStatus status);

    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :studentId ORDER BY c.createdAt DESC")
    List<Course> findRecentCoursesByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(c) FROM Course c JOIN c.students s WHERE s.id = :studentId")
    int countCoursesByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :studentId")
    List<Course> findCoursesByStudentId(@Param("studentId") Long studentId);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end); // Changed to LocalDateTime

    @Query("SELECT MIN(a.createdAt) FROM Account a")
    LocalDate getFirstCreatedDate();
    List<Course> findByStatus(ECourseStatus status);
    // Add this method to count courses by category
    @Query("SELECT COUNT(c) FROM Course c WHERE c.category.id = :categoryId")
    int countByCategoryId(Long categoryId);

    // For better performance with multiple categories, add this method
    @Query("SELECT c.category.id as categoryId, COUNT(c) as count FROM Course c GROUP BY c.category.id")
    List<Object[]> countCoursesGroupByCategory();

    @Query("SELECT COUNT(c) FROM Course c WHERE c.instructor.id = :instructorId AND c.status = :status")
    int countByInstructorIdAndStatus(@Param("instructorId") Long instructorId, @Param("status") String status);

    @Query("SELECT AVG(c.duration) FROM Course c WHERE c.instructor.id = :instructorId")
    Double getAverageCourseDurationByInstructorId(@Param("instructorId") Long instructorId);

    Page<Course> findByInstructorId(Long instructorId, Pageable pageable);

    Page<Course> findByInstructorIdAndStatus(Long instructor_id, ECourseStatus status, Pageable pageable);

}

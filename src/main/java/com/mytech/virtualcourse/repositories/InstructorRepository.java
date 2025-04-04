package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Date;
@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    Optional<Instructor> findByAccountId(Long accountId);

    boolean existsInstructorByAccountId(Long accountId);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.instructor.id = :instructorId")
    int countCoursesByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.instructor.id = :instructorId AND c.status = 'PUBLISHED'")
    int countPublishedCoursesByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.instructor.id = :instructorId AND c.status = 'PENDING'")
    int countPendingCoursesByInstructorId(@Param("instructorId") Long instructorId);


    @Query("SELECT COUNT(DISTINCT s.id) FROM Course c JOIN c.students s WHERE c.instructor.id = :instructorId")
    int countStudentsInInstructorCourses(@Param("instructorId") Long instructorId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r JOIN r.course c WHERE c.instructor.id = :instructorId")
    double calculateAverageRatingByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT MIN(a.createdAt) FROM Account a")
    LocalDate getFirstCreatedDate();

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end); // Changed to LocalDateTime
}

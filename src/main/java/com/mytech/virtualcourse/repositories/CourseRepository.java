package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Course findByTitleCourse(String titleCourse);

    boolean existsByTitleCourse(String titleCourse);


    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :studentId ORDER BY c.createdAt DESC")
    List<Course> findRecentCoursesByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(c) FROM Course c JOIN c.students s WHERE s.id = :studentId")
    int countCoursesByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :studentId")
    List<Course> findCoursesByStudentId(@Param("studentId") Long studentId);


}

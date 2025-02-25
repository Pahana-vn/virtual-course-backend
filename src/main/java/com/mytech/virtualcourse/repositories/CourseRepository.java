package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.dtos.CourseDTO;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.enums.ECourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

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

    @Query("SELECT c FROM Course c WHERE c.category.id = :categoryId")
    List<Course> findByCategoryId(@Param("categoryId") Long categoryId);

    long countByCreatedAtBetween(Date start, Date end);

    @Query("SELECT c FROM Course c WHERE c.category.id = :categoryId ORDER BY c.createdAt DESC")
    List<Course> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE "
            + "(:categoryIds IS NULL OR c.category.id IN :categoryIds) AND "
            + "(:instructorIds IS NULL OR c.instructor.id IN :instructorIds) AND "
            + "(:minPrice IS NULL OR c.basePrice >= :minPrice) AND "
            + "(:maxPrice IS NULL OR (c.basePrice <= :maxPrice)) AND "
            + "(:search IS NULL OR LOWER(c.titleCourse) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Course> findFilteredCourses(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("instructorIds") List<Long> instructorIds,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT c FROM Course c WHERE " +
            "LOWER(c.titleCourse) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.instructor.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.instructor.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Course> searchCoursesFlutter(@Param("keyword") String keyword);
    // aaads

}

package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.dtos.CourseDTO;
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
public interface  CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByInstructor(Instructor instructor);

    List<Course> findByStatus(ECourseStatus status);

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

    @Query("SELECT c FROM Course c WHERE c.category.id = :categoryId")
    List<Course> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(s) FROM Section s WHERE s.course.id = :courseId")
    int countTotalSections(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(l) FROM Lecture l WHERE l.section.course.id = :courseId")
    int countTotalLectures(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(a) FROM Article a WHERE a.lecture.section.course.id = :courseId")
    int countTotalArticles(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.course.id = :courseId")
    int countTotalQuestions(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(DISTINCT s) FROM Student s JOIN s.courses c WHERE c.id = :courseId")
    int countTotalPurchasedStudents(@Param("courseId") Long courseId);

    @Query("SELECT c FROM Course c WHERE c.category.id = :categoryId ORDER BY c.createdAt DESC")
    List<Course> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Course c " +
            "JOIN c.students s " +
            "WHERE c.instructor.id = :instructorId")
    List<Course> findPurchasedCoursesByInstructor(@Param("instructorId") Long instructorId);

    @Query("SELECT c FROM Course c WHERE "
            + "(:categoryIds IS NULL OR c.category.id IN :categoryIds) AND "
            + "(:instructorIds IS NULL OR c.instructor.id IN :instructorIds) AND "
            + "(:minPrice IS NULL OR c.basePrice >= :minPrice) AND "
            + "(:maxPrice IS NULL OR (c.basePrice <= :maxPrice)) AND "
            + "(:search IS NULL OR LOWER(c.titleCourse) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND "
            + "(:status IS NULL OR c.status = :status)"
    )

    Page<Course> findFilteredCourses(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("instructorIds") List<Long> instructorIds,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("search") String search,
            @Param("status") ECourseStatus status,
            Pageable pageable);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.category.id = :categoryId AND c.status = 'PUBLISHED'")
    int countPublishedCoursesByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT c FROM Course c WHERE " +
            "LOWER(c.titleCourse) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.instructor.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.instructor.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Course> searchCoursesFlutter(@Param("keyword") String keyword);

}

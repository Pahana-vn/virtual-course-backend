package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.StudentLectureProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface StudentLectureProgressRepository extends JpaRepository<StudentLectureProgress, Long> {

    StudentLectureProgress findByStudentIdAndLectureId(Long studentId, Long lectureId);

    // Đếm số bài giảng đã hoàn thành trong một khóa học
    @Query("SELECT COUNT(slp) FROM StudentLectureProgress slp " +
            "JOIN slp.lecture l " +
            "JOIN l.section sec " +
            "JOIN sec.course c " +
            "WHERE slp.student.id = :studentId AND c.id = :courseId AND slp.completed = true")
    int countCompletedLecturesByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    // Lấy danh sách ID các bài giảng đã hoàn thành
    @Query("SELECT slp.lecture.id FROM StudentLectureProgress slp " +
            "JOIN slp.lecture l " +
            "JOIN l.section sec " +
            "JOIN sec.course c " +
            "WHERE slp.student.id = :studentId AND c.id = :courseId AND slp.completed = true")
    List<Long> findCompletedLectureIdsByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}

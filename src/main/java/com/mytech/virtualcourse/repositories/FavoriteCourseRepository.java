package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.FavoriteCourse;
import com.mytech.virtualcourse.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteCourseRepository extends JpaRepository<FavoriteCourse, Long> {
    List<FavoriteCourse> findByStudent(Student student);
    Optional<FavoriteCourse> findByStudentAndCourse(Student student, Course course);
}

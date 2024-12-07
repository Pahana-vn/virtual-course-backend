package com.mytech.virtualcourse.repositories;
import com.mytech.virtualcourse.entities.Student;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.FavoriteCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<FavoriteCourse, Long> {

    boolean existsByStudentAndCourse(Student student, Course course);
}

package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    Instructor findByAccountId(Long accountId);

    boolean existsInstructorByAccountId(Long accountId);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.instructor.id = :instructorId")
    Long countCoursesByInstructorId(@Param("instructorId") Long instructorId);

    @Query("SELECT COUNT(DISTINCT s.id) FROM Course c JOIN c.students s WHERE c.instructor.id = :instructorId")
    Long countStudentsInInstructorCourses(@Param("instructorId") Long instructorId);


}

package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByAccountId(Long accountId);

    @Query("SELECT COUNT(c) FROM Student s JOIN s.courses c WHERE s.id = ?1")
    int countRegisteredCourses(Long studentId);


}

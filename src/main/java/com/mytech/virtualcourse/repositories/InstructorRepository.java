package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Date;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    long countByCreatedAtBetween(Date createdAt, Date createdAt2);

}

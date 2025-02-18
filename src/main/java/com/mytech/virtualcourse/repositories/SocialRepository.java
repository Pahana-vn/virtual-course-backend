package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Social;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialRepository extends JpaRepository<Social, Long> {
    Optional<Social> findByInstructorId(Long instructorId);
}

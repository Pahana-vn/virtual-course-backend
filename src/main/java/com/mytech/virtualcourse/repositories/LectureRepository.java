package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
}

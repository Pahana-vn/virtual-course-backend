package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.StudentAnswer;
import com.mytech.virtualcourse.entities.StudentTestSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long>{
}


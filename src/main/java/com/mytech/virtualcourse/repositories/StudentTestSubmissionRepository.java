package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.StudentTestSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentTestSubmissionRepository extends JpaRepository<StudentTestSubmission, Long> {
    Optional<StudentTestSubmission> findByTestIdAndStudentId(Long testId, Long studentId);
    Optional<StudentTestSubmission> findTopByTestIdAndStudentIdOrderByMarksObtainedDesc(Long testId, Long studentId);
    List<StudentTestSubmission> findByStudentId(Long studentId);
    Optional<StudentTestSubmission> findById(Long quizId);
    List<StudentTestSubmission> findByTestId(Long testId);
}


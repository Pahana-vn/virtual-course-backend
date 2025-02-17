package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCourseId(Long courseId);
    @Query("SELECT q FROM Question q JOIN q.tests t WHERE t.id = :testId")
    List<Question> findByTestId(@Param("testId") Long testId);

}

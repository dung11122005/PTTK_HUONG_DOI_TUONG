package com.example.exam_portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exam_portal.domain.Question;


@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>{
    Question save(Question question);

    void deleteById(long id);

    Question findById(long id);

    List<Question> findByExamId(Long examId);
}

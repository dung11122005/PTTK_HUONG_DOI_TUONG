package com.example.exam_portal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exam_portal.domain.ExamResult;


@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long>{

    Page<ExamResult> findByExamSessionId(Long id, Pageable pageable);

    boolean existsByStudentIdAndExamId(Long studentId, Long examId);

    List<ExamResult> findByExamSessionId(Long Id);
}

package com.example.exam_portal.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.exam_portal.domain.ExamResult;


@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long>{

    Page<ExamResult> findByExamSessionId(Long id, Pageable pageable);

    boolean existsByStudentIdAndExamId(Long studentId, Long examId);

    List<ExamResult> findByExamSessionId(Long Id);

    void deleteById(long id);



    @Query(value = """
        SELECT 
            s.id AS studentId,
            s.full_name AS studentName,
            er.score AS score
        FROM exam_results er
        JOIN users s ON s.id = er.student_id
        JOIN exams e ON e.id = er.exam_id
        WHERE e.exam_session_id = :examSessionId
        ORDER BY s.full_name ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findScoresByExamSession(Long examSessionId);




}

package com.example.exam_portal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.ExamResult;
import com.example.exam_portal.repository.ExamResultRepository;


@Service
public class ExamResultService {
    private final ExamResultRepository examResultRepository;

    public ExamResultService(ExamResultRepository examResultRepository){
        this.examResultRepository=examResultRepository;
    }

    public Page<ExamResult> getAllResulrExamPaginationTeacherId(Long id, Pageable page) {
        return this.examResultRepository.findByExamSessionId(id, page);
    }

    public Page<ExamResult> getAllResulrExamPagination(Pageable page) {
        return this.examResultRepository.findAll(page);
    }

    public List<ExamResult> getAllExamResultSessionId(Long SessionId){
        return this.examResultRepository.findByExamSessionId(SessionId);
    }

    public ExamResult handleSaveExam(ExamResult exam) {
        ExamResult eric = this.examResultRepository.save(exam);
        return eric;
    }

    public boolean hasStudentSubmittedExam(Long studentId, Long examId) {
        return examResultRepository.existsByStudentIdAndExamId(studentId, examId);
    }
}

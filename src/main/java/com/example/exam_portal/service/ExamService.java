package com.example.exam_portal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.Exam;
import com.example.exam_portal.domain.Subject;
import com.example.exam_portal.domain.enums.ExamType;
import com.example.exam_portal.repository.ExamRepository;

@Service
public class ExamService {
    
    private final ExamRepository examRepository;

    public ExamService(ExamRepository examRepository){
        this.examRepository=examRepository;
    }

    public Page<Exam> getAllExamByType(ExamType examType, Pageable pageable) {
        return examRepository.findByExamType(examType, pageable);
    }

    public Page<Exam> getAllExamByTypeAndSubject(ExamType examType, Subject subject, Pageable pageable) {
        return examRepository.findByExamTypeAndSubject(examType, subject, pageable);
    }


    public Page<Exam> getAllExamByTypeAndTeacherId(ExamType examType, Long teacherId, Pageable pageable) {
        return examRepository.findByExamTypeAndCreatedBy_Id(examType, teacherId, pageable);
    }

    public Page<Exam> getAllExamPaginationTeacherId(Long id, Pageable page) {
        return this.examRepository.findByCreatedBy_Id(id, page);
    }

    public Page<Exam> getAllExamPagination(Pageable page) {
        return this.examRepository.findAll(page);
    }

    public List<Exam> getAllExam() {
        return this.examRepository.findAll();
    }

    public Exam handleSaveExam(Exam exam) {
        Exam eric = this.examRepository.save(exam);
        return eric;
    }

    public Exam getExamById(long id) {
        return this.examRepository.findById(id);
    }

    public List<Exam> getExamByTeacherId(long id) {
        return this.examRepository.findByCreatedBy_Id(id);
    }

    public void deleteAExam(long id) {
        this.examRepository.deleteById(id);
    }
}

package com.example.exam_portal.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.Exam;
import com.example.exam_portal.repository.ExamRepository;

@Service
public class ExamService {
    
    private final ExamRepository examRepository;

    public ExamService(ExamRepository examRepository){
        this.examRepository=examRepository;
    }

    public Page<Exam> getAllExamPagination(Pageable page) {
        return this.examRepository.findAll(page);
    }

    public Exam handleSaveExam(Exam exam) {
        Exam eric = this.examRepository.save(exam);
        return eric;
    }

    public Exam getExamById(long id) {
        return this.examRepository.findById(id);
    }

    public void deleteAExam(long id) {
        this.examRepository.deleteById(id);
    }
}

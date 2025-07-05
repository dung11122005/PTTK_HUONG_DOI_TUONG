package com.example.exam_portal.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.ExamSession;
import com.example.exam_portal.repository.TestRepositoty;

@Service
public class TestService {
    
    private final TestRepositoty testRepositoty;

    public TestService(TestRepositoty testRepositoty){
        this.testRepositoty=testRepositoty;
    }

    public Page<ExamSession> getAllExamSessionPaginationTeacherId(Long id, Pageable page) {
        return this.testRepositoty.findByTeacherId(id, page);
    }

    public Page<ExamSession> getAllExamSessionPagination(Pageable page) {
        return this.testRepositoty.findAll(page);
    }

    public ExamSession handleSaveExamSession(ExamSession examSession) {
        ExamSession eric = this.testRepositoty.save(examSession);
        return eric;
    }

    public ExamSession getExamSessionById(long id) {
        return this.testRepositoty.findById(id);
    }

    public void deleteAExamSession(long id) {
        this.testRepositoty.deleteById(id);
    }
}

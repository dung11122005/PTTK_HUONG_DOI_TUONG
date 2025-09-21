package com.example.exam_portal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.ExamSession;
import com.example.exam_portal.repository.TestRepository;

@Service
public class TestService {
    
    private final TestRepository testRepositoty;

    public TestService(TestRepository testRepositoty){
        this.testRepositoty=testRepositoty;
    }

    public Page<ExamSession> getAllExamSessionByClassIds(List<Long> classIds, Pageable pageable) {
        return this.testRepositoty.findByClassroom_IdIn(classIds, pageable);
    }

    public Page<ExamSession> getAllExamSessionPaginationTeacherId(Long id, Pageable page) {
        return this.testRepositoty.findByCreatedBy_Id(id, page);
    }

    public Page<ExamSession> getAllExamSessionPagination(Pageable page) {
        return this.testRepositoty.findAll(page);
    }

    public List<ExamSession> getAllExamSession() {
        return this.testRepositoty.findAll();
    }

    public List<ExamSession> getAllExamSessionListClassId(List<Long> classIds) {
        return this.testRepositoty.findByClassroom_IdIn(classIds);
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

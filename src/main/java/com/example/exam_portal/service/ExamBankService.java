package com.example.exam_portal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.ExamBank;
import com.example.exam_portal.repository.ExamBankRepository;


@Service
public class ExamBankService {

    private final ExamBankRepository examBankRepository;

    public ExamBankService(ExamBankRepository examBankRepository){
        this.examBankRepository=examBankRepository;
    }

    public Page<ExamBank> getAllExamPagination(Pageable page) {
        return this.examBankRepository.findAll(page);
    }

    public List<ExamBank> getAllExam() {
        return this.examBankRepository.findAll();
    }

    public ExamBank handleSaveExam(ExamBank exam) {
        ExamBank eric = this.examBankRepository.save(exam);
        return eric;
    }

    public Optional<ExamBank> getExamById(long id) {
        return this.examBankRepository.findById(id);
    }

    public void deleteAExam(long id) {
        this.examBankRepository.deleteById(id);
    }
}

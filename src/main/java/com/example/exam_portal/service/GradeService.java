package com.example.exam_portal.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.Grade;
import com.example.exam_portal.repository.GradeRepository;


@Service
public class GradeService {
    private final GradeRepository gradeRepository;


    public GradeService(GradeRepository gradeRepository){
        this.gradeRepository=gradeRepository;
    }

    public Grade handleSaveGrade(Grade Grade) {
        Grade year = this.gradeRepository.save(Grade);
        return year;
    }


    public Optional<Grade> getGradeById(long id) {
        return this.gradeRepository.findById(id);
    }

    public void deleteGrade(long id) {
        this.gradeRepository.deleteById(id);
    }

    public Page<Grade> getAllGradePagination(Pageable page) {
        return this.gradeRepository.findAll(page);
    }
}

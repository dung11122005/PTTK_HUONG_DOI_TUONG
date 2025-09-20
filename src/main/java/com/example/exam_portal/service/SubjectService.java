package com.example.exam_portal.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.Subject;
import com.example.exam_portal.repository.SubjectRepository;


@Service
public class SubjectService {
    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository){
        this.subjectRepository=subjectRepository;
    }

    public Subject handleSaveSubject(Subject subject) {
        Subject su = this.subjectRepository.save(subject);
        return su;
    }


    public Optional<Subject> getSubjectById(long id) {
        return this.subjectRepository.findById(id);
    }

    public void deleteSubject(long id) {
        this.subjectRepository.deleteById(id);
    }

    public Page<Subject> getAllSubjectPagination(Pageable page) {
        return this.subjectRepository.findAll(page);
    }
}

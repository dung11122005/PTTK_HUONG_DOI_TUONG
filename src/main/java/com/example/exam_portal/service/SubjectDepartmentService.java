package com.example.exam_portal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.SubjectDepartment;
import com.example.exam_portal.repository.SubjectDepartmentRepository;


@Service
public class SubjectDepartmentService {
    private final SubjectDepartmentRepository subjectDepartmentRepository;

    public SubjectDepartmentService(SubjectDepartmentRepository subjectDepartmentRepository){
        this.subjectDepartmentRepository=subjectDepartmentRepository;
    }

    public List<SubjectDepartment> getAllSubjectDepartment() {
        return this.subjectDepartmentRepository.findAll();
    }

    public SubjectDepartment handleSaveSubjectDepartment(SubjectDepartment subject) {
        SubjectDepartment su = this.subjectDepartmentRepository.save(subject);
        return su;
    }


    public Optional<SubjectDepartment> getSubjectDepartmentById(long id) {
        return this.subjectDepartmentRepository.findById(id);
    }

    public void deleteSubjectDepartment(long id) {
        this.subjectDepartmentRepository.deleteById(id);
    }

    public Page<SubjectDepartment> getAllSubjectDepartmentPagination(Pageable page) {
        return this.subjectDepartmentRepository.findAll(page);
    }
}

package com.example.exam_portal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.Student;
import com.example.exam_portal.repository.StudentRepository;


@Service
public class StudentService {
    private final StudentRepository studentRepository;


    public StudentService(StudentRepository studentRepository){
        this.studentRepository=studentRepository;
    }

    public List<Student> getAllStudent() {
        return this.studentRepository.findAll();
    }

    public Student handleSaveStudent(Student student) {
        Student year = this.studentRepository.save(student);
        return year;
    }


    public Optional<Student> getStudentById(long id) {
        return this.studentRepository.findById(id);
    }

    public void deleteStudent(long id) {
        this.studentRepository.deleteById(id);
    }

    public Page<Student> getAllStudentPagination(Pageable page) {
        return this.studentRepository.findAll(page);
    } 
}

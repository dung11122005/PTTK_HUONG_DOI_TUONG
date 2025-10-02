package com.example.exam_portal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.Teacher;
import com.example.exam_portal.repository.TeacherRepository;


@Service
public class TeacherService {
    private final TeacherRepository teacherRepository;


    public TeacherService(TeacherRepository teacherRepository){
        this.teacherRepository=teacherRepository;
    }

    public List<Teacher> getAllTeacher() {
        return this.teacherRepository.findAll();
    }

    public Teacher handleSaveTeacher(Teacher teacher) {
        Teacher te = this.teacherRepository.save(teacher);
        return te;
    }


    public Optional<Teacher> getTeacherById(long id) {
        return this.teacherRepository.findById(id);
    }

    public void deleteTeacher(long id) {
        this.teacherRepository.deleteById(id);
    }

    public Page<Teacher> getAllTeacherPagination(Pageable page) {
        return this.teacherRepository.findAll(page);
    }
}

package com.example.exam_portal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.ClassRoom;
import com.example.exam_portal.domain.ClassStudent;
import com.example.exam_portal.repository.ClassRepository;
import com.example.exam_portal.repository.ClassStudentRepository;

import jakarta.transaction.Transactional;

@Service
public class ClassService {
    
    private final ClassRepository classRepository;
    private final ClassStudentRepository classStudentRepository;

    public ClassService(ClassRepository classRepository, ClassStudentRepository classStudentRepository){
        this.classRepository=classRepository;
        this.classStudentRepository=classStudentRepository;
    }

    public Page<ClassRoom> getAllClassRoomPaginationByIdTeacher(Long id, Pageable page) {
        return this.classRepository.findByTeacherId(id, page);
    }

    public Page<ClassRoom> getAllClassRoomPagination(Pageable page) {
        return this.classRepository.findAll(page);
    }

    public ClassRoom handleSaveClassRoom(ClassRoom classrRoom) {
        ClassRoom eric = this.classRepository.save(classrRoom);
        return eric;
    }

    public ClassRoom getClassRoomById(long id) {
        return this.classRepository.findById(id);
    }

    public List<ClassStudent> getClassStudentById(long id) {
        return this.classStudentRepository.findByClassroom_Id(id);
    }

    public List<ClassStudent> getClassRoomByClassIdAndStudentId(Long classId, Long studentId) {
        return this.classStudentRepository.findByClassroom_IdAndStudent_Id(classId, studentId);
    }

    public void deleteAClassRoom(long id) {
        this.classRepository.deleteById(id);
    }

    @Transactional
    public void deleteAClassAndStudent(Long classId, Long studentId) {
        this.classStudentRepository.deleteByClassroom_IdAndStudent_Id(classId, studentId);
    }
}

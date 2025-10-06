package com.example.exam_portal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.TeachingAssignment;
import com.example.exam_portal.repository.TeachingAssignmentRepository;


@Service
public class TeachingAssignmentService {
    private final TeachingAssignmentRepository teachingAssignmentRepository;

    public TeachingAssignmentService(TeachingAssignmentRepository teachingAssignmentRepository){
        this.teachingAssignmentRepository=teachingAssignmentRepository;
    }


    public TeachingAssignment handleSaveTeachingAssignment(TeachingAssignment teachingAssignment) {
        TeachingAssignment year = this.teachingAssignmentRepository.save(teachingAssignment);
        return year;
    }


    public Optional<TeachingAssignment> getTeachingAssignmentById(long id) {
        return this.teachingAssignmentRepository.findById(id);
    }

    public void deleteTeachingAssignment(long id) {
        this.teachingAssignmentRepository.deleteById(id);
    }

    public Page<TeachingAssignment> getAllTeachingAssignmentPagination(Pageable page) {
        return this.teachingAssignmentRepository.findAll(page);
    }

    // 🔹 Lấy tất cả phân công theo lớp
    public List<TeachingAssignment> getByClassRoomId(Long classId) {
        return teachingAssignmentRepository.findByClassroom_Id(classId);
    }

    // 🔹 Xóa toàn bộ phân công theo lớp
    public void deleteByClassRoomId(Long classId) {
        teachingAssignmentRepository.deleteByClassroom_Id(classId);
    }

    // 🔹 Lấy phân công theo giáo viên
    public List<TeachingAssignment> getByTeacherId(Long teacherId) {
        return teachingAssignmentRepository.findByTeacher_Id(teacherId);
    }

    // 🔹 Lấy phân công theo môn
    public List<TeachingAssignment> getBySubjectId(Long subjectId) {
        return teachingAssignmentRepository.findBySubject_Id(subjectId);
    }

    // 🔹 Tìm phân công theo lớp + giáo viên + môn (check trùng)
    // public Optional<TeachingAssignment> findUnique(Long classId, Long teacherId, Long subjectId) {
    //     return teachingAssignmentRepository.findByClassroom_IdAndTeacher_IdAndSubject_Id(classId, teacherId, subjectId);
    // }


}

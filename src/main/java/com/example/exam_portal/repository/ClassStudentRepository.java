package com.example.exam_portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.exam_portal.domain.ClassRoom;
import com.example.exam_portal.domain.ClassStudent;


@Repository
public interface  ClassStudentRepository extends JpaRepository<ClassStudent, Long>, JpaSpecificationExecutor<ClassStudent>{
    List<ClassStudent> findByClassroom_IdAndStudent_Id(Long classId, Long studentId);

    List<ClassStudent> findByClassroom_Id(Long id);

    void deleteByClassroom_IdAndStudent_Id(Long classId, Long studentId);

    List<ClassStudent> findByClassroomIn(List<ClassRoom> classrooms);

    List<ClassStudent> findByStudent_Id(Long studentId);

}

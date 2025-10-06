package com.example.exam_portal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exam_portal.domain.TeachingAssignment;

@Repository
public interface TeachingAssignmentRepository extends JpaRepository<TeachingAssignment, Long> {
    Page<TeachingAssignment> findByTeacher_Id(Long teacherId, Pageable pageable);

    List<TeachingAssignment> findByClassroom_Id(Long classId);

    void deleteByClassroom_Id(Long classId);

    List<TeachingAssignment> findByTeacher_Id(Long teacherId);

}


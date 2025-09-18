package com.example.exam_portal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exam_portal.domain.ClassRoom;

@Repository
public interface ClassRepository extends JpaRepository<ClassRoom, Long>{
    ClassRoom save(ClassRoom classRoom);

    List<ClassRoom> findAll();

    List<ClassRoom> findByAssignments_Teacher_Id(Long id);

    Page<ClassRoom> findByAssignments_Teacher_Id(Long teacherId, Pageable pageable);

    ClassRoom findById(long id);
    
}

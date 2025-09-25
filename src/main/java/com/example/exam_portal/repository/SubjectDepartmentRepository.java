package com.example.exam_portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.exam_portal.domain.SubjectDepartment;

@Repository
public interface SubjectDepartmentRepository extends JpaRepository<SubjectDepartment, Long>, JpaSpecificationExecutor<SubjectDepartment>{
    
}

package com.example.exam_portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exam_portal.domain.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long>{
    
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

}

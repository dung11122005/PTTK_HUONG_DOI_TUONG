package com.example.exam_portal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exam_portal.domain.Course;
import com.example.exam_portal.domain.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long>{
    
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    Page<Purchase> findByCourseIn(List<Course> courses, Pageable pageable);

    List<Purchase> findByStudentId(Long id);

}

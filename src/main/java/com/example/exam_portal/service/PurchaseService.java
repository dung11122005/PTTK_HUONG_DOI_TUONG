package com.example.exam_portal.service;

import org.springframework.stereotype.Service;

import com.example.exam_portal.repository.PurchaseRepository;


@Service
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;

    public PurchaseService(PurchaseRepository purchaseRepository){
        this.purchaseRepository=purchaseRepository;
    }

    public boolean checkPurchaseStudentIdAndCourseId(Long studentId, Long courseId){
        return this.purchaseRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }
}

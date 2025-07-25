package com.example.exam_portal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.Course;
import com.example.exam_portal.domain.Purchase;
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

    public void handleSavePurchase(Purchase purchase){
        this.purchaseRepository.save(purchase);
    }

    public Page<Purchase> getAllCourseSoldPaginationListCourseId(List<Course> courses, Pageable page) {
        return this.purchaseRepository.findByCourseIn(courses, page);
    }

    public Page<Purchase> getAllCourseSoldPagination(Pageable page) {
        return this.purchaseRepository.findAll(page);
    }

    public List<Purchase> getPurchaseClientByStudentId(Long studentId) {
        return this.purchaseRepository.findByStudentId(studentId);
    }
}

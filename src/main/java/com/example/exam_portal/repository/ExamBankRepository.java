package com.example.exam_portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exam_portal.domain.ExamBank;



@Repository
public interface  ExamBankRepository  extends JpaRepository<ExamBank, Long>{
    
}

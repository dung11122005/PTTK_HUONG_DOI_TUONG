package com.example.exam_portal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exam_portal.domain.Exam;


@Repository
public interface ExamRepository extends JpaRepository<Exam, Long>{

    Exam save(Exam hoidanit);

    List<Exam> findAll();

    List<Exam> findByCreatedBy_Id(Long id);

    Page<Exam> findByCreatedBy_Id(Long id, Pageable pageable);

    Exam findById(long id);
}

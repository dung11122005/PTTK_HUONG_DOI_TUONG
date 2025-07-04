package com.example.exam_portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exam_portal.domain.Exam;


@Repository
public interface ExamRepository extends JpaRepository<Exam, Long>{

    Exam save(Exam hoidanit);

    List<Exam> findAll();

    Exam findById(long id);
}

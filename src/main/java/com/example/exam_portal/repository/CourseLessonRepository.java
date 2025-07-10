package com.example.exam_portal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exam_portal.domain.CourseLesson;


@Repository
public interface CourseLessonRepository extends JpaRepository<CourseLesson, Long>{
    Optional<CourseLesson> findById(Long id);
}

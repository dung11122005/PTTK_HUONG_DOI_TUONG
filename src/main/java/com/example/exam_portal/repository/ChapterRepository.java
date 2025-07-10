package com.example.exam_portal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exam_portal.domain.Chapter;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long>{

    List<Chapter> findAll();

    Optional<Chapter> findById(Long id);

}

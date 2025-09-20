package com.example.exam_portal.domain.dto;

public record ExamDTO(
    Long id,
    String name,
    Long gradeId,
    Long subjectId,
    String subjectName,
    String examType
) {}

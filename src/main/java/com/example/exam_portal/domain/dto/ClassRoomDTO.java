package com.example.exam_portal.domain.dto;

public record ClassRoomDTO(
    Long id,
    String name,
    Long gradeId,
    Long academicYearId,
    String classCode
) {}

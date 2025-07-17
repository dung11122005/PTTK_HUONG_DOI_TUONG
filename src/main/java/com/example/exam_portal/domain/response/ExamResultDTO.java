package com.example.exam_portal.domain.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamResultDTO {
    private Long studentId;
    private String studentName;
    private Float score;
    private LocalDateTime submittedAt;

}

package com.example.exam_portal.domain.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamResultListResponse {
    private String examName;
    private List<ExamResultDTO> results;
}

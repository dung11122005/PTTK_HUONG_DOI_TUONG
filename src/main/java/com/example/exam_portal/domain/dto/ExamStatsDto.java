package com.example.exam_portal.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExamStatsDto {
    private Long examId;       // ở đây chính là examSessionId
    private String examName;   // examSessionName
    private Double avgScore;   // có thể bỏ qua hoặc để = 0.0
    private Long totalResults; // số lượng bài thi
}

package com.example.exam_portal.controller.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exam_portal.domain.ExamResult;
import com.example.exam_portal.domain.response.ExamResultDTO;
import com.example.exam_portal.domain.response.ExamResultListResponse;
import com.example.exam_portal.domain.response.RestResponse;
import com.example.exam_portal.service.ExamResultService;
import com.example.exam_portal.util.annotation.ApiMessage;
import com.example.exam_portal.util.error.IdInvalidException;




@RestController
@RequestMapping("/api/v1")
public class ListResultExamController {
    private final ExamResultService examResultService;

    public ListResultExamController(ExamResultService examResultService){
        this.examResultService=examResultService;
    }


    @GetMapping("/listresult/{examSessionId}")
    @ApiMessage("Fetch exam result list")
    public ResponseEntity<RestResponse<ExamResultListResponse>> getExamResultList(@PathVariable("examSessionId") long id) throws IdInvalidException{
        List<ExamResult> examResults = examResultService.getAllExamResultSessionId(id);

        if (examResults == null || examResults.isEmpty()) {
            throw new IdInvalidException("ExamSession với id = " + id + " không tồn tại hoặc không có kết quả nào.");
        }
    
        // Lấy examName từ 1 kết quả bất kỳ vì cùng kỳ thi
        String examName = examResults.get(0).getExam().getName();

        // Mapping sang DTO
        List<ExamResultDTO> dtos = examResults.stream().map(result -> {
            ExamResultDTO dto = new ExamResultDTO();
            dto.setStudentId(result.getStudent().getId());
            dto.setStudentName(result.getStudent().getFullName());
            dto.setScore(result.getScore());
            dto.setSubmittedAt(result.getSubmittedAt());
            return dto;
        }).collect(Collectors.toList());

        // Gộp vào wrapper response
        ExamResultListResponse data = new ExamResultListResponse();
        data.setExamName(examName);
        data.setResults(dtos);

        // Bọc vào RestResponse
        RestResponse<ExamResultListResponse> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Lấy danh sách kết quả thành công.");
        res.setData(data);
        res.setError(null);

        return ResponseEntity.ok(res);
    }
    
}

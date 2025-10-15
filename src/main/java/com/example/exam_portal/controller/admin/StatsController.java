package com.example.exam_portal.controller.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exam_portal.domain.AcademicYear;
import com.example.exam_portal.service.AcademicYearService;
import com.example.exam_portal.service.ClassService;
import com.example.exam_portal.service.ExamResultService;
import com.example.exam_portal.service.StatisticsService;
import com.example.exam_portal.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Controller
public class StatsController {
    private final StatisticsService stats;
    private final ObjectMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(StatsController.class);
    private final UserService userService;
    private final ClassService classService;
    private final ExamResultService examResultService;
    private final AcademicYearService academicYearService;


    public StatsController(StatisticsService stats, ObjectMapper mapper,
    UserService userService, ClassService classService, ExamResultService examResultService,
    AcademicYearService academicYearService) {
        this.stats = stats;
        this.mapper = mapper;
        this.userService=userService;
        this.classService=classService;
        this.examResultService=examResultService;
        this.academicYearService=academicYearService;
    }

    @GetMapping("/admin/stats/principal")
    public String principalStats(Model model, @RequestParam(required = false) Long yearId) {
        

        List<AcademicYear> academicYears = academicYearService.getAllAcademicYear();
        Long currentYearId = yearId;
        // default safe values
        List<Map<String, Object>> avgByGrade = Collections.emptyList();
        List<Map<String, Object>> passBySubject = Collections.emptyList();
        List<Map<String, Object>> topStudents = Collections.emptyList();
        List<Map<String, Object>> examsMonthly = Collections.emptyList();
        List<Map<String, Object>> examsByTeacher = Collections.emptyList();
        List<Map<String, Object>> classPerformance = Collections.emptyList();
        List<Map<String, Object>> avgDurationBySubject = Collections.emptyList();
        List<Map<String, Object>> scoreDistribution = Collections.emptyList();

        // fetch data (catch any exception from service layer and log)
        try {
            List<Map<String, Object>> t;

            t = stats.avgScoreByGrade(currentYearId);
            if (t != null) avgByGrade = t;

            t = stats.passRateBySubject(currentYearId, 5.0);
            if (t != null) passBySubject = t;

            t = stats.topStudents(currentYearId, 10);
            if (t != null) topStudents = t;

            t = stats.examsPerMonth(12);
            if (t != null) examsMonthly = t;

            t = stats.examsPerTeacher(10);
            if (t != null) examsByTeacher = t;
            
            // Thêm biểu đồ mới
            t = stats.classPerformance(currentYearId);
            if (t != null) classPerformance = t;
            
            t = stats.avgDurationBySubject();
            if (t != null) avgDurationBySubject = t;
            
            t = stats.schoolScoreDistribution();
            if (t != null) scoreDistribution = t;
            
        } catch (Exception e) {
            // log full stacktrace for debugging root cause
            logger.error("Error while fetching statistics for principal dashboard", e);
        }

        try {
            // Điểm trung bình toàn trường
            Double avgScore = stats.getSchoolAvgScore(currentYearId);
            model.addAttribute("avgScore", avgScore != null ? String.format("%.1f", avgScore) : null);
            
            // Tỷ lệ đạt toàn trường (với điểm đạt 5.0)
            Double passRate = stats.getSchoolPassRate(currentYearId, 5.0);
            model.addAttribute("passRate", passRate != null ? String.format("%.1f", passRate) : null);
            
            // Tổng số kỳ thi
            Long examCount = stats.getExamCount(currentYearId);
            model.addAttribute("examCount", examCount);
            
            // Học sinh tham gia
            Long studentCount = stats.getStudentCount(currentYearId);
            model.addAttribute("studentCount", studentCount);
            
        } catch (Exception e) {
            logger.error("Error calculating dashboard KPIs", e);
        }
            try {
            Map<String, String> aiInsight = new HashMap<>();
            aiInsight.put("title", "Phân tích dữ liệu học tập");
            aiInsight.put("content", "Dựa trên dữ liệu hiện có, kết quả học tập của học sinh đang ở mức khá với điểm trung bình 7.2. Các khối 10 và 11 có kết quả tốt hơn khối 12. Môn Toán và Tiếng Anh cần được chú ý hơn do tỷ lệ đạt thấp hơn so với các môn khác.");
            
            model.addAttribute("aiInsight", aiInsight);
        } catch (Exception e) {
            logger.error("Error generating AI insight", e);
        }

        // safe JSON serialization helper (returns "[]" on failure)
        model.addAttribute("avgByGradeData", safeWrite(avgByGrade));
        model.addAttribute("passBySubjectData", safeWrite(passBySubject));
        model.addAttribute("topStudents", topStudents); // server-side rendered table
        model.addAttribute("examsMonthlyData", safeWrite(examsMonthly));
        model.addAttribute("examsByTeacherData", safeWrite(examsByTeacher));
        
        // Thêm dữ liệu biểu đồ mới
        model.addAttribute("classPerformanceData", safeWrite(classPerformance));
        model.addAttribute("avgDurationData", safeWrite(avgDurationBySubject));
        model.addAttribute("scoreDistributionData", safeWrite(scoreDistribution));
        model.addAttribute("academicYears", academicYears);
        model.addAttribute("selectedYearId", currentYearId);

        return "admin/stats/principal";
    }

    


    @GetMapping("/admin/exam/result/statistics/{examSessionId}")
    public String getExamStatistics(@PathVariable Long examSessionId, Model model) {
        // Phổ điểm
        Map<String, Long> distribution = examResultService.getScoreDistribution(examSessionId);

        // Dữ liệu cho biểu đồ
        List<String> labels = new ArrayList<>(distribution.keySet());
        List<Long> counts = new ArrayList<>(distribution.values());

        String labelsJson = new Gson().toJson(labels);
        String countsJson = new Gson().toJson(counts);

        double avgScore = examResultService.getAverageScore(examSessionId);

        model.addAttribute("examSessionId", examSessionId);
        model.addAttribute("avgScore", avgScore);
        model.addAttribute("labelsJson", labelsJson);
        model.addAttribute("countsJson", countsJson);

        return "client/statistics/statistics";
    }






    private String safeWrite(Object obj) {
        try {
            return mapper.writeValueAsString(obj == null ? Collections.emptyList() : obj);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize object for charts", e);
            return "[]";
        } catch (Exception e) {
            logger.error("Unexpected error while serializing object", e);
            return "[]";
        }
    }
}

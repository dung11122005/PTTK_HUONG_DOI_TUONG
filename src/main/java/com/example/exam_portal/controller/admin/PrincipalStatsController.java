package com.example.exam_portal.controller.admin;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.exam_portal.service.StatisticsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class PrincipalStatsController {
    private final StatisticsService stats;
    private final ObjectMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(PrincipalStatsController.class);

    public PrincipalStatsController(StatisticsService stats, ObjectMapper mapper) {
        this.stats = stats;
        this.mapper = mapper;
    }

    @GetMapping("/admin/stats/principal")
    public String principalStats(Model model) {
        Long currentYearId = null; // hoặc lấy từ config / session / param

        // default safe values
        List<Map<String, Object>> avgByGrade = Collections.emptyList();
        List<Map<String, Object>> passBySubject = Collections.emptyList();
        List<Map<String, Object>> topStudents = Collections.emptyList();
        List<Map<String, Object>> examsMonthly = Collections.emptyList();
        List<Map<String, Object>> examsByTeacher = Collections.emptyList();

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
        } catch (Exception e) {
            // log full stacktrace for debugging root cause
            logger.error("Error while fetching statistics for principal dashboard", e);
        }

        // safe JSON serialization helper (returns "[]" on failure)
        model.addAttribute("avgByGradeData", safeWrite(avgByGrade));
        model.addAttribute("passBySubjectData", safeWrite(passBySubject));
        model.addAttribute("topStudents", topStudents); // server-side rendered table
        model.addAttribute("examsMonthlyData", safeWrite(examsMonthly));
        model.addAttribute("examsByTeacherData", safeWrite(examsByTeacher));

        return "admin/stats/principal";
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

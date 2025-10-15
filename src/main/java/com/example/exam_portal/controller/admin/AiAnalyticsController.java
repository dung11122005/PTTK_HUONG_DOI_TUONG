package com.example.exam_portal.controller.admin;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.exam_portal.service.AiAnalyticsService;

@RestController
public class AiAnalyticsController {

    private final AiAnalyticsService aiAnalyticsService;
    
    public AiAnalyticsController(AiAnalyticsService aiAnalyticsService) {
        this.aiAnalyticsService = aiAnalyticsService;
    }
    
    @GetMapping("/api/ai/dashboard-insights")
    public ResponseEntity<Map<String, Object>> getDashboardInsights(
            @RequestParam(required = false) Long yearId) {
        
        Map<String, Object> insights = aiAnalyticsService.generateDashboardInsights(yearId);
        return ResponseEntity.ok(insights);
    }
}
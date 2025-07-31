package com.example.exam_portal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.exam_portal.client.GeminiClient;
import com.example.exam_portal.domain.FeedbackResult;
import com.example.exam_portal.domain.PromptBuilder;
import com.example.exam_portal.util.ResponseParser;


@Service
public class PracticeService {
    @Autowired
    private GeminiClient geminiClient;

    public String generateRandomVietnameseSentence() {
        return geminiClient.ask("Hãy tạo một câu tiếng Việt đơn giản (~20–25 từ) cho học sinh dịch sang tiếng Anh. Chỉ trả về câu tiếng Việt.");
    }

    public FeedbackResult analyzeStudentTranslation(String sourceVi, String studentEn) {
        String prompt = PromptBuilder.buildCorrectionPrompt(sourceVi, studentEn);
        String json = geminiClient.askRaw(prompt);
        return ResponseParser.parseFeedback(json);
    }

 
}

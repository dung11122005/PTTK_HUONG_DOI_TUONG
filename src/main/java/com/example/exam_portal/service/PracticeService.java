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
        return this.geminiClient.ask("Hãy tạo một đoạn tiếng Việt bất cứ chủ đề gì và những câu bạn trả ra không được lặp lại câu trước (~100–120 từ) cho học sinh dịch sang tiếng Anh. Chỉ trả về câu tiếng Việt.");
    }

    public FeedbackResult analyzeStudentTranslation(String sourceVi, String studentEn) {
        String prompt = PromptBuilder.buildCorrectionPrompt(sourceVi, studentEn);
        String json = this.geminiClient.askRaw(prompt);
        return ResponseParser.parseFeedback(json);
    }

 
}

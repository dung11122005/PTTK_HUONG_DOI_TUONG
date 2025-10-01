package com.example.exam_portal.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.exam_portal.domain.response.FeedbackResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseParser {
    public static String extractText(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);
            return node.path("candidates").get(0)
                       .path("content").path("parts").get(0)
                       .path("text").asText();
        } catch (Exception e) {
            return "Lỗi đọc phản hồi: " + e.getMessage();
        }
    }

    public static String markdownToHtml(String text) {
        return text
            .replaceAll("\\*\\*([^*]+)\\*\\*", "<strong>$1</strong>")   // **bold**
            .replaceAll("(?m)^\\* (.+)$", "<li>$1</li>")                // bullet
            .replaceAll("\\n", "<br>");                                 // xuống dòng
    }

    public static FeedbackResult parseFeedback(String json) {
        FeedbackResult result = new FeedbackResult();
        String text = extractText(json); // cái này bạn chắc chắn có
        result.setRawResponse(text);
        
        Pattern p = Pattern.compile(
            "- ✅.*?:\\s*(.*?)\\n- ❌.*?:\\s*(.*?)\\n- 📊.*?:\\s*(.*?)\\n- 🔧.*?:\\s*(.*)",
            Pattern.DOTALL);
        Matcher m = p.matcher(text);
        if (m.find()) {
            result.setStandardTranslation(m.group(1).trim());
            result.setErrorAnalysis(m.group(2).trim());
            result.setScore(m.group(3).trim());
            result.setSuggestedFix(m.group(4).trim());
        
            // ✅ Chuyển markdown sang HTML
            result.setErrorAnalysisHtml(markdownToHtml(result.getErrorAnalysis()));
            result.setSuggestedFixHtml(markdownToHtml(result.getSuggestedFix()));
        }
    
        return result;
    }


    
}

package com.example.exam_portal.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiAnalyticsService {
    private final Logger logger = LoggerFactory.getLogger(AiAnalyticsService.class);
    private final StatisticsService statisticsService;
    
    // Di chuyển biến ENDPOINT ra ngoài phương thức
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    
    @Value("${gemini.api.key}")
    private String geminiApiKey;
    
    public AiAnalyticsService(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }
    
    public Map<String, Object> generateDashboardInsights(Long yearId) {
        Map<String, Object> insights = new HashMap<>();
        
        try {
            // 1. Thu thập dữ liệu từ StatisticsService
            List<Map<String, Object>> avgByGrade = statisticsService.avgScoreByGrade(yearId);
            List<Map<String, Object>> passBySubject = statisticsService.passRateBySubject(yearId, 5.0);
            List<Map<String, Object>> classPerformance = statisticsService.classPerformance(yearId);
            
            // 2. Phân tích tổng quan
            String summary = analyzeOverview(avgByGrade, passBySubject, classPerformance);
            insights.put("summary", summary);
            
            // 3. Phát hiện các xu hướng đáng chú ý
            List<Map<String, String>> findings = detectNotablePatterns(avgByGrade, passBySubject, classPerformance);
            insights.put("findings", findings);
            
            // 4. Đưa ra các khuyến nghị
            List<Map<String, String>> recommendations = generateRecommendations(avgByGrade, passBySubject, classPerformance);
            insights.put("recommendations", recommendations);
            
            // 5. Phát hiện bất thường trong dữ liệu
            Map<String, String> anomalies = detectAnomalies(avgByGrade, 2.0);
            if (anomalies != null) {
                if (findings == null) findings = new ArrayList<>();
                findings.add(anomalies);
            }
            
            // 6. Thêm phân tích AI từ Gemini
            Map<String, String> aiInsight = getAIAnalysis(avgByGrade);
            if (aiInsight != null) {
                insights.put("aiInsight", aiInsight);
            }
            
        } catch (Exception e) {
            logger.error("Error generating dashboard insights", e);
            insights.put("error", "Đã xảy ra lỗi khi phân tích dữ liệu: " + e.getMessage());
        }
        
        return insights;
    }
    
    private String analyzeOverview(List<Map<String, Object>> avgByGrade, 
                                  List<Map<String, Object>> passBySubject,
                                  List<Map<String, Object>> classPerformance) {
        // Phân tích tổng quan dựa trên dữ liệu
        double overallAvg = calculateOverallAverage(avgByGrade);
        double overallPassRate = calculateOverallPassRate(passBySubject);
        
        if (overallAvg >= 7.0) {
            return "Nhà trường đang có kết quả học tập tốt với điểm trung bình " + overallAvg + 
                   " và tỷ lệ đạt " + overallPassRate + "%. Các số liệu cho thấy việc dạy và học đang diễn ra hiệu quả.";
        } else if (overallAvg >= 5.0) {
            return "Nhà trường có kết quả học tập ở mức trung bình với điểm số " + overallAvg + 
                   " và tỷ lệ đạt " + overallPassRate + "%. Cần có những cải thiện để nâng cao chất lượng.";
        } else {
            return "Nhà trường đang có kết quả học tập dưới mức trung bình với điểm số " + overallAvg + 
                   " và tỷ lệ đạt " + overallPassRate + "%. Cần có những biện pháp cải thiện khẩn cấp.";
        }
    }
    
    private List<Map<String, String>> detectNotablePatterns(List<Map<String, Object>> avgByGrade,
                                                     List<Map<String, Object>> passBySubject,
                                                     List<Map<String, Object>> classPerformance) {
        List<Map<String, String>> findings = new ArrayList<>();
        
        // Phát hiện khối có điểm cao nhất và thấp nhất
        Map<String, String> gradePerformance = findHighestAndLowestGrades(avgByGrade);
        if (gradePerformance != null) {
            findings.add(gradePerformance);
        }
        
        // Phát hiện môn học có tỷ lệ đạt thấp
        Map<String, String> subjectConcern = findSubjectsOfConcern(passBySubject);
        if (subjectConcern != null) {
            findings.add(subjectConcern);
        }
        
        // Phân tích sự chênh lệch giữa các lớp
        Map<String, String> classDisparity = analyzeClassDisparity(classPerformance);
        if (classDisparity != null) {
            findings.add(classDisparity);
        }
        
        return findings;
    }
    
    private List<Map<String, String>> generateRecommendations(List<Map<String, Object>> avgByGrade,
                                                           List<Map<String, Object>> passBySubject,
                                                           List<Map<String, Object>> classPerformance) {
        List<Map<String, String>> recommendations = new ArrayList<>();
        
        // Phân tích dữ liệu để đưa ra khuyến nghị dựa trên dữ liệu thực tế
        if (avgByGrade != null && !avgByGrade.isEmpty()) {
            // Tìm khối có điểm thấp nhất
            String lowestGrade = "";
            double lowestScore = Double.MAX_VALUE;
            
            for (Map<String, Object> grade : avgByGrade) {
                String name = grade.get("label").toString();
                double score = ((Number) grade.get("value")).doubleValue();
                
                if (score < lowestScore) {
                    lowestScore = score;
                    lowestGrade = name;
                }
            }
            
            if (!lowestGrade.isEmpty()) {
                Map<String, String> rec1 = new HashMap<>();
                rec1.put("title", "Cải thiện kết quả khối " + lowestGrade);
                rec1.put("content", "Tổ chức các buổi học bổ sung cho học sinh khối " + lowestGrade + 
                         ", đặc biệt là các môn có tỷ lệ đạt thấp.");
                recommendations.add(rec1);
            }
        }
        
        // Tạo khuyến nghị dựa trên sự chênh lệch giữa các lớp
        if (classPerformance != null && classPerformance.size() >= 2) {
            Map<String, String> rec2 = new HashMap<>();
            rec2.put("title", "Giảm chênh lệch giữa các lớp");
            rec2.put("content", "Áp dụng phương pháp giảng dạy thành công từ các lớp có kết quả tốt sang các lớp có kết quả thấp hơn.");
            recommendations.add(rec2);
        }
        
        // Khuyến nghị chung về phát triển đội ngũ giáo viên
        Map<String, String> rec3 = new HashMap<>();
        rec3.put("title", "Tăng cường hỗ trợ giáo viên");
        rec3.put("content", "Tổ chức các buổi chia sẻ kinh nghiệm giảng dạy giữa các giáo viên để học hỏi từ những người có kết quả tốt.");
        recommendations.add(rec3);
        
        return recommendations;
    }
    
    private double calculateOverallAverage(List<Map<String, Object>> avgByGrade) {
        // Logic tính điểm trung bình toàn trường
        if (avgByGrade == null || avgByGrade.isEmpty()) {
            return 0;
        }
        
        double sum = 0;
        int count = 0;
        for (Map<String, Object> grade : avgByGrade) {
            Object value = grade.get("value");
            if (value instanceof Number) {
                sum += ((Number) value).doubleValue();
                count++;
            }
        }
        return count > 0 ? Math.round(sum / count * 10) / 10.0 : 0;
    }
    
    private double calculateOverallPassRate(List<Map<String, Object>> passBySubject) {
        // Logic tính tỷ lệ đạt trung bình
        if (passBySubject == null || passBySubject.isEmpty()) {
            return 0;
        }
        
        double sum = 0;
        int count = 0;
        for (Map<String, Object> subject : passBySubject) {
            Object value = subject.get("value");
            if (value instanceof Number) {
                sum += ((Number) value).doubleValue();
                count++;
            }
        }
        return count > 0 ? Math.round(sum / count * 10) / 10.0 : 0;
    }
    
    private Map<String, String> findHighestAndLowestGrades(List<Map<String, Object>> avgByGrade) {
        if (avgByGrade == null || avgByGrade.isEmpty()) {
            return null;
        }
        
        String highestGrade = "";
        double highestScore = Double.MIN_VALUE;
        String lowestGrade = "";
        double lowestScore = Double.MAX_VALUE;
        
        for (Map<String, Object> grade : avgByGrade) {
            String name = grade.get("label").toString();
            double score = ((Number) grade.get("value")).doubleValue();
            
            if (score > highestScore) {
                highestScore = score;
                highestGrade = name;
            }
            
            if (score < lowestScore) {
                lowestScore = score;
                lowestGrade = name;
            }
        }
        
        Map<String, String> result = new HashMap<>();
        result.put("title", "Phân tích theo khối");
        
        String scoreDifference = String.format("%.1f", highestScore - lowestScore);
        String content = String.format("Khối %s có điểm trung bình cao nhất (%.1f) và khối %s có điểm thấp nhất (%.1f). ", 
                                       highestGrade, highestScore, lowestGrade, lowestScore);
        
        if (highestScore - lowestScore > 1.0) {
            content += "Chênh lệch giữa các khối là đáng kể (" + scoreDifference + " điểm). Cần đặc biệt chú ý đến việc cải thiện kết quả khối " + lowestGrade + ".";
        } else {
            content += "Chênh lệch giữa các khối là không đáng kể.";
        }
        
        result.put("content", content);
        return result;
    }
    
    private Map<String, String> findSubjectsOfConcern(List<Map<String, Object>> passBySubject) {
        if (passBySubject == null || passBySubject.isEmpty()) {
            return null;
        }
        
        String lowestSubject = "";
        double lowestRate = Double.MAX_VALUE;
        
        for (Map<String, Object> subject : passBySubject) {
            String name = subject.get("label").toString();
            double rate = ((Number) subject.get("value")).doubleValue();
            
            if (rate < lowestRate) {
                lowestRate = rate;
                lowestSubject = name;
            }
        }
        
        Map<String, String> result = new HashMap<>();
        result.put("title", "Môn học cần quan tâm");
        
        if (lowestRate < 70.0) {
            result.put("content", String.format("Môn %s có tỷ lệ đạt thấp nhất (%.1f%%). Cần xem xét lại phương pháp giảng dạy và tài liệu học tập cho môn này.", 
                                          lowestSubject, lowestRate));
        } else {
            result.put("content", String.format("Tỷ lệ đạt các môn học đều ở mức tốt. Môn %s có tỷ lệ thấp nhất nhưng vẫn đạt %.1f%%.", 
                                          lowestSubject, lowestRate));
        }
        
        return result;
    }
    
    private Map<String, String> analyzeClassDisparity(List<Map<String, Object>> classPerformance) {
        if (classPerformance == null || classPerformance.size() < 2) {
            return null;
        }
        
        String highestClass = "";
        double highestScore = Double.MIN_VALUE;
        String lowestClass = "";
        double lowestScore = Double.MAX_VALUE;
        
        for (Map<String, Object> cls : classPerformance) {
            String name = cls.get("label").toString();
            double score = ((Number) cls.get("value")).doubleValue();
            
            if (score > highestScore) {
                highestScore = score;
                highestClass = name;
            }
            
            if (score < lowestScore) {
                lowestScore = score;
                lowestClass = name;
            }
        }
        
        double difference = highestScore - lowestScore;
        Map<String, String> result = new HashMap<>();
        result.put("title", "Chênh lệch giữa các lớp");
        
        if (difference > 1.5) {
            result.put("content", String.format("Có sự chênh lệch lớn giữa các lớp. Lớp %s có điểm trung bình cao nhất (%.1f) và lớp %s có điểm thấp nhất (%.1f), chênh lệch %.1f điểm.", 
                                          highestClass, highestScore, lowestClass, lowestScore, difference));
        } else if (difference > 0.5) {
            result.put("content", String.format("Có sự chênh lệch đáng kể giữa các lớp. Lớp %s (%.1f) và lớp %s (%.1f) có chênh lệch %.1f điểm.", 
                                          highestClass, highestScore, lowestClass, lowestScore, difference));
        } else {
            result.put("content", "Không có sự chênh lệch đáng kể giữa các lớp. Điều này cho thấy chất lượng giảng dạy khá đồng đều.");
        }
        
        return result;
    }
    
    private Map<String, String> analyzeTrends(List<Map<String, Object>> historicalData) {
        if (historicalData == null || historicalData.size() < 2) {
            return null;
        }
        
        // Phân tích xu hướng dữ liệu theo thời gian
        // Sử dụng thuật toán linear regression đơn giản để dự đoán xu hướng
        
        double[] x = new double[historicalData.size()];
        double[] y = new double[historicalData.size()];
        
        for (int i = 0; i < historicalData.size(); i++) {
            x[i] = i;
            y[i] = ((Number) historicalData.get(i).get("value")).doubleValue();
        }
        
        // Tính toán hệ số hồi quy tuyến tính
        double slope = calculateSlope(x, y);
        
        Map<String, String> trend = new HashMap<>();
        trend.put("title", "Xu hướng kết quả học tập");
        
        if (slope > 0.1) {
            trend.put("content", "Kết quả học tập đang có xu hướng cải thiện rõ rệt. Các biện pháp hiện tại đang phát huy hiệu quả.");
        } else if (slope > 0) {
            trend.put("content", "Kết quả học tập đang có xu hướng cải thiện nhẹ. Cần tiếp tục duy trì các biện pháp hiện tại.");
        } else if (slope > -0.1) {
            trend.put("content", "Kết quả học tập đang ổn định, không có nhiều thay đổi.");
        } else {
            trend.put("content", "Kết quả học tập đang có xu hướng giảm. Cần có các biện pháp can thiệp kịp thời.");
        }
        
        return trend;
    }
    
    private double calculateSlope(double[] x, double[] y) {
        // Thuật toán tính hệ số góc của đường thẳng hồi quy
        int n = x.length;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
        }
        
        return (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
    }
    
    private Map<String, String> detectAnomalies(List<Map<String, Object>> data, double threshold) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        
        // Tính mean và standard deviation
        double sum = 0;
        for (Map<String, Object> item : data) {
            Object value = item.get("value");
            if (value instanceof Number) {
                sum += ((Number) value).doubleValue();
            }
        }
        
        double mean = sum / data.size();
        
        double variance = 0;
        for (Map<String, Object> item : data) {
            Object valueObj = item.get("value");
            if (valueObj instanceof Number) {
                double value = ((Number) valueObj).doubleValue();
                variance += Math.pow(value - mean, 2);
            }
        }
        
        double stdDev = Math.sqrt(variance / data.size());
        
        // Phát hiện các điểm bất thường (nằm ngoài ngưỡng)
        List<String> anomalies = new ArrayList<>();
        for (Map<String, Object> item : data) {
            String label = item.get("label").toString();
            Object valueObj = item.get("value");
            
            if (valueObj instanceof Number) {
                double value = ((Number) valueObj).doubleValue();
                if (Math.abs(value - mean) > threshold * stdDev) {
                    anomalies.add(String.format("%s (%.1f)", label, value));
                }
            }
        }
        
        if (anomalies.isEmpty()) {
            return null;
        }
        
        Map<String, String> result = new HashMap<>();
        result.put("title", "Phát hiện bất thường");
        result.put("content", "Phát hiện các điểm dữ liệu bất thường có thể cần điều tra thêm: " + String.join(", ", anomalies));
        
        return result;
    }
    
    // Thêm phương thức generateBasicAnalysis bị thiếu
    private String generateBasicAnalysis(List<Map<String, Object>> data) {
        if (data == null || data.isEmpty()) {
            return "Không đủ dữ liệu để phân tích.";
        }
        
        // Tính điểm trung bình
        double sum = 0;
        int count = 0;
        for (Map<String, Object> item : data) {
            Object valueObj = item.get("value");
            if (valueObj instanceof Number) {
                sum += ((Number) valueObj).doubleValue();
                count++;
            }
        }
        
        if (count == 0) {
            return "Không thể tính toán với dữ liệu hiện có.";
        }
        
        double avg = sum / count;
        
        // Tạo phân tích cơ bản
        StringBuilder analysis = new StringBuilder();
        analysis.append(String.format("Điểm trung bình là %.2f. ", avg));
        
        if (avg >= 8.0) {
            analysis.append("Kết quả học tập của học sinh đang ở mức tốt. ");
            analysis.append("Cần tiếp tục duy trì và phát huy phương pháp giảng dạy hiện tại.");
        } else if (avg >= 6.5) {
            analysis.append("Kết quả học tập của học sinh ở mức khá. ");
            analysis.append("Cần có một số điều chỉnh nhỏ để cải thiện điểm số.");
        } else if (avg >= 5.0) {
            analysis.append("Kết quả học tập của học sinh ở mức trung bình. ");
            analysis.append("Cần xem xét lại phương pháp giảng dạy và có biện pháp hỗ trợ học sinh.");
        } else {
            analysis.append("Kết quả học tập của học sinh đang ở mức thấp. ");
            analysis.append("Cần có những biện pháp cải thiện khẩn cấp và hỗ trợ tích cực cho học sinh.");
        }
        
        return analysis.toString();
    }
    
    private Map<String, String> getAIAnalysis(List<Map<String, Object>> data) {
        // Chuẩn bị dữ liệu để gửi tới API
        StringBuilder prompt = new StringBuilder();
        prompt.append("Dưới đây là dữ liệu về kết quả học tập của học sinh:\n\n");
        
        for (Map<String, Object> item : data) {
            prompt.append(item.get("label") + ": " + item.get("value") + "\n");
        }
        
        prompt.append("\nDựa trên dữ liệu trên, hãy phân tích và đưa ra nhận định về tình hình học tập cũng như đề xuất cải thiện. Phân tích ngắn gọn và cụ thể.");
        
        // Gọi Gemini API
        try {
            String apiKey = geminiApiKey;
            if (apiKey == null || apiKey.isEmpty()) {
                logger.warn("Gemini API key not configured");
                Map<String, String> fallbackResult = new HashMap<>();
                fallbackResult.put("title", "Phân tích AI không khả dụng");
                fallbackResult.put("content", "API key cho dịch vụ AI chưa được cấu hình. Vui lòng liên hệ quản trị viên.");
                return fallbackResult;
            }
            
            // URL đúng cho API Gemini
            String fullUrl = ENDPOINT + "?key=" + apiKey;
            
            // Tạo request body
            String requestBody = "{"
                + "\"contents\": [{"
                + "  \"parts\": [{"
                + "    \"text\": \"" + prompt.toString().replace("\"", "\\\"").replace("\n", "\\n") + "\""
                + "  }]"
                + "}],"
                + "\"generationConfig\": {"
                + "  \"temperature\": 0.4,"
                + "  \"maxOutputTokens\": 1000"
                + "}"
                + "}";
            
            // Gọi API
            URL url = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            
            // Debug log
            logger.debug("Calling Gemini API with URL: " + fullUrl);
            logger.debug("Request body: " + requestBody);
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Kiểm tra response code trước khi đọc response
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                // Đọc error message từ server
                StringBuilder errorResponse = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorResponse.append(errorLine.trim());
                    }
                }
                logger.error("API responded with error code: " + responseCode);
                logger.error("Error response: " + errorResponse.toString());
                throw new RuntimeException("API responded with error code: " + responseCode);
            }
            
            // Đọc response thành công
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            
            // Debug log
            logger.debug("API response: " + response.toString());
            
            // Parse JSON response
            String responseStr = response.toString();
            int textStart = responseStr.indexOf("\"text\":\"");
            if (textStart != -1) {
                int contentStart = textStart + 8; // 8 là độ dài của "\"text\":\"" 
                int contentEnd = responseStr.indexOf("\"", contentStart);
                if (contentEnd != -1) {
                    String aiContent = responseStr.substring(contentStart, contentEnd);
                    
                    Map<String, String> result = new HashMap<>();
                    result.put("title", "Phân tích AI");
                    result.put("content", aiContent);
                    return result;
                }
            }
            
            // Thử cách khác nếu không tìm thấy "text"
            textStart = responseStr.indexOf("\"content\":{");
            if (textStart != -1) {
                int partStart = responseStr.indexOf("\"text\":\"", textStart);
                if (partStart != -1) {
                    int contentStart = partStart + 8;
                    int contentEnd = responseStr.indexOf("\"", contentStart);
                    if (contentEnd != -1) {
                        String aiContent = responseStr.substring(contentStart, contentEnd);
                        
                        Map<String, String> result = new HashMap<>();
                        result.put("title", "Phân tích AI");
                        result.put("content", aiContent);
                        return result;
                    }
                }
            }
            
            throw new RuntimeException("Không thể trích xuất nội dung từ phản hồi API");
            
        } catch (Exception e) {
            // Log lỗi và trả về phân tích thay thế
            logger.error("Error while getting AI analysis from Gemini", e);
            
            // Tạo phân tích thay thế dựa trên dữ liệu
            String analysis = generateBasicAnalysis(data);
            Map<String, String> errorResult = new HashMap<>();
            errorResult.put("title", "Phân tích dựa trên dữ liệu");
            errorResult.put("content", analysis);
            return errorResult;
        }
    }
}
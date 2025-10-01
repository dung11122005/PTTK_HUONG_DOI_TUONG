package com.example.exam_portal.domain.response;

public class PromptBuilder {
    public static String buildCorrectionPrompt(String vi, String en) {
        return """
        Bạn đóng vai giáo viên dạy tiếng Anh, chuyên phân tích lỗi của học sinh.
            
        ### Câu tiếng Việt gốc:
        %s
            
        ### Câu học sinh viết bằng tiếng Anh:
        %s
            
        Hãy thực hiện các bước sau:
        1. Dịch câu gốc sang tiếng Anh chuẩn và tự nhiên.
        2. So sánh bản dịch chuẩn với câu học sinh viết:
           - Phân tích lỗi ngữ pháp
           - Lỗi từ vựng hoặc cách dùng từ không tự nhiên
           - Lỗi logic hoặc diễn đạt chưa đúng nghĩa
        3. Chấm điểm cho câu của học sinh (thang điểm 0–10).
        4. Gợi ý bản sửa chính xác cho câu học sinh đã viết.
            
        Phản hồi rõ ràng theo định dạng:
        - ✅ Bản dịch chuẩn:
        - ❌ Phân tích lỗi:
        - 📊 Điểm số:
        - 🔧 Gợi ý sửa:

        Vui lòng giữ nguyên cấu trúc dòng như trên để hệ thống có thể xử lý.
        """.formatted(vi, en);
    }
}

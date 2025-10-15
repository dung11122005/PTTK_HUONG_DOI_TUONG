package com.example.exam_portal.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class StatisticsService {
    private final Logger logger = LoggerFactory.getLogger(StatisticsService.class);
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public StatisticsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    // 1) Điểm trung bình theo khối
    public List<Map<String,Object>> avgScoreByGrade(Long academicYearId) {
        String sql = """
            SELECT g.name AS label, ROUND(AVG(er.score),2) AS value
            FROM exam_results er
            JOIN exams e ON e.id = er.exam_id
            JOIN grades g ON g.id = e.grade_id
            GROUP BY g.id, g.name
            ORDER BY g.order_index
            """;
        return jdbcTemplate.queryForList(sql);
    }

    // 2) Tỷ lệ đạt theo môn học
    public List<Map<String,Object>> passRateBySubject(Long academicYearId, double passThreshold) {
        String sql = """
            SELECT s.name AS label,
                   ROUND(100.0 * SUM(CASE WHEN er.score >= :pass THEN 1 ELSE 0 END) / NULLIF(COUNT(*),0),2) AS value
            FROM exam_results er
            JOIN exams e ON e.id = er.exam_id
            JOIN subjects s ON s.id = e.subject_id
            GROUP BY s.id, s.name
            ORDER BY value DESC
            """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("pass", passThreshold);
        
        return namedJdbcTemplate.queryForList(sql, params);
    }

    // 3) Top N học sinh theo điểm trung bình
    public List<Map<String,Object>> topStudents(Long academicYearId, int limit) {
        String sql = """
            SELECT u.id, u.full_name AS label, ROUND(AVG(er.score),2) AS value
            FROM exam_results er
            JOIN users u ON u.id = er.student_id
            GROUP BY u.id, u.full_name
            ORDER BY value DESC
            LIMIT :limit
            """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("limit", limit);
        
        return namedJdbcTemplate.queryForList(sql, params);
    }

    // 4) Số đề thi theo tháng (12 tháng gần nhất)
    public List<Map<String,Object>> examsPerMonth(int monthsBack) {
        String sql;
        
        // H2 Database (dành cho phát triển)
        if (isH2Database()) {
            sql = """
                SELECT FORMATDATETIME(e.created_at, 'yyyy-MM') AS label, COUNT(*) AS value
                FROM exams e
                WHERE e.created_at >= DATEADD('MONTH', -:months, CURRENT_DATE())
                GROUP BY FORMATDATETIME(e.created_at, 'yyyy-MM')
                ORDER BY label
                """;
        } 
        // MySQL
        else {
            sql = """
                SELECT DATE_FORMAT(e.created_at, '%Y-%m') AS label, COUNT(*) AS value
                FROM exams e
                WHERE e.created_at >= DATE_SUB(CURDATE(), INTERVAL :months MONTH)
                GROUP BY DATE_FORMAT(e.created_at, '%Y-%m')
                ORDER BY label
                """;
        }
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("months", monthsBack);
        
        return namedJdbcTemplate.queryForList(sql, params);
    }

    // 5) Số đề thi theo giáo viên (top 10)
    public List<Map<String,Object>> examsPerTeacher(int limit) {
        String sql = """
            SELECT u.full_name AS label, COUNT(e.id) AS value
            FROM exams e
            JOIN users u ON u.id = e.created_by
            GROUP BY u.id, u.full_name
            ORDER BY value DESC
            LIMIT :limit
            """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("limit", limit);
        
        return namedJdbcTemplate.queryForList(sql, params);
    }

    public List<Map<String,Object>> classPerformance(Long academicYearId) {
        String sql = """
            SELECT c.name AS label, ROUND(AVG(er.score),2) AS value
            FROM exam_results er
            JOIN exam_sessions es ON er.exam_session_id = es.id
            JOIN classes c ON es.class_id = c.id
            JOIN exams e ON er.exam_id = e.id
            GROUP BY c.id, c.name
            ORDER BY value DESC
            """;
        
        return jdbcTemplate.queryForList(sql);
    }


    public List<Map<String,Object>> scoreProgressByYear() {
        String sql = """
            SELECT ay.name AS label, ROUND(AVG(er.score),2) AS value
            FROM exam_results er
            JOIN exam_sessions es ON er.exam_session_id = es.id
            JOIN academic_years ay ON es.academic_year_id = ay.id
            GROUP BY ay.id, ay.name
            ORDER BY ay.start_year
            """;

        return jdbcTemplate.queryForList(sql);
    }


    public List<Map<String,Object>> avgDurationBySubject() {
        String sql = """
            SELECT s.name AS label, ROUND(AVG(er.duration_used)/60,2) AS value
            FROM exam_results er
            JOIN exams e ON er.exam_id = e.id
            JOIN subjects s ON e.subject_id = s.id
            GROUP BY s.id, s.name
            ORDER BY value DESC
            """;

        return jdbcTemplate.queryForList(sql);
    }



    public List<Map<String,Object>> teacherEffectiveness() {
        String sql = """
            SELECT u.full_name AS label, s.name AS subject, ROUND(AVG(er.score),2) AS value
            FROM exam_results er
            JOIN exams e ON er.exam_id = e.id
            JOIN users u ON e.created_by = u.id
            JOIN subjects s ON e.subject_id = s.id
            GROUP BY u.id, u.full_name, s.id, s.name
            ORDER BY s.name, value DESC
            """;
        
        // Sửa lỗi: Thêm tham số thứ hai là Map rỗng
        return namedJdbcTemplate.queryForList(sql, new MapSqlParameterSource());
    }


    public List<Map<String,Object>> schoolScoreDistribution() {
        String sql = """
            SELECT 
                CASE 
                    WHEN score < 5 THEN 'Dưới 5'
                    WHEN score >= 5 AND score < 6.5 THEN '5.0-6.4'
                    WHEN score >= 6.5 AND score < 8 THEN '6.5-7.9'
                    WHEN score >= 8 AND score < 9 THEN '8.0-8.9' 
                    ELSE '9.0-10' 
                END AS label,
                COUNT(*) AS value
            FROM exam_results
            GROUP BY label
            ORDER BY MIN(score)
            """;

        return jdbcTemplate.queryForList(sql);
    }

    // Lấy điểm trung bình toàn trường
    public Double getSchoolAvgScore(Long yearId) {
        // Sửa lại tên bảng để phù hợp với schema
        String sql = "SELECT AVG(er.score) FROM exam_results er " +
                 "JOIN exam_sessions es ON er.exam_session_id = es.id " +
                 "JOIN exams e ON es.exam_id = e.id " +
                 (yearId != null ? "WHERE e.academic_year_id = :yearId" : "");
                 
        try {
            if (yearId != null) {
                MapSqlParameterSource params = new MapSqlParameterSource();
                params.addValue("yearId", yearId);
                return namedJdbcTemplate.queryForObject(sql, params, Double.class);
            } else {
                return jdbcTemplate.queryForObject(sql, Double.class);
            }
        } catch (Exception e) {
            logger.error("Error calculating school average score", e);
            return null;
        }
    }

    // Lấy tỷ lệ đạt toàn trường
    public Double getSchoolPassRate(Long yearId, double passThreshold) {
        // Sửa lại tên bảng
        String sql = "SELECT (COUNT(CASE WHEN er.score >= :threshold THEN 1 END) * 100.0 / COUNT(*)) " +
                 "FROM exam_results er " +
                 "JOIN exam_sessions es ON er.exam_session_id = es.id " +
                 "JOIN exams e ON es.exam_id = e.id " +
                 (yearId != null ? "WHERE e.academic_year_id = :yearId" : "");
                 
        try {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("threshold", passThreshold);
            if (yearId != null) {
                params.addValue("yearId", yearId);
            }
            return namedJdbcTemplate.queryForObject(sql, params, Double.class);
        } catch (Exception e) {
            logger.error("Error calculating school pass rate", e);
            return null;
        }
    }

    // Lấy số kỳ thi
    public Long getExamCount(Long yearId) {
        // Sửa lại tên bảng
        String sql = "SELECT COUNT(DISTINCT es.id) FROM exam_sessions es " +
                 "JOIN exams e ON es.exam_id = e.id " +
                 (yearId != null ? "WHERE e.academic_year_id = :yearId" : "");
                 
        try {
            if (yearId != null) {
                MapSqlParameterSource params = new MapSqlParameterSource();
                params.addValue("yearId", yearId);
                return namedJdbcTemplate.queryForObject(sql, params, Long.class);
            } else {
                return jdbcTemplate.queryForObject(sql, Long.class);
            }
        } catch (Exception e) {
            // Sửa lỗi System.Logger thành logger
            logger.error("Error calculating exam count", e);
            return null;
        }
    }

    // Lấy số học sinh tham gia
    public Long getStudentCount(Long yearId) {
        // Sửa lại tên bảng
        String sql = "SELECT COUNT(DISTINCT er.student_id) FROM exam_results er " +
                 "JOIN exam_sessions es ON er.exam_session_id = es.id " +
                 "JOIN exams e ON es.exam_id = e.id " +
                 (yearId != null ? "WHERE e.academic_year_id = :yearId" : "");
                 
        try {
            if (yearId != null) {
                MapSqlParameterSource params = new MapSqlParameterSource();
                params.addValue("yearId", yearId);
                return namedJdbcTemplate.queryForObject(sql, params, Long.class);
            } else {
                return jdbcTemplate.queryForObject(sql, Long.class);
            }
        } catch (Exception e) {
            logger.error("Error calculating student count", e);
            return null;
        }
    }

    // Phương thức kiểm tra loại database đang sử dụng
    private boolean isH2Database() {
        try {
            String dbProductName = jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductName();
            return dbProductName.contains("H2");
        } catch (Exception e) {
            return false;
        }
    }
}
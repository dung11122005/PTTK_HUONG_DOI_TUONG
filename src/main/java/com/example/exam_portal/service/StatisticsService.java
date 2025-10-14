package com.example.exam_portal.service;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class StatisticsService {
    private final JdbcTemplate jdbc;

    public StatisticsService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // 1) Avg score per grade for given academic year id
    public List<Map<String,Object>> avgScoreByGrade(Long academicYearId) {
        String sql = """
            SELECT g.name AS label, ROUND(AVG(r.score),2) AS value
            FROM results r
            JOIN grades g ON g.id = r.grade_id
            WHERE (:yearId IS NULL OR r.academic_year_id = :yearId)
            GROUP BY g.id, g.name
            ORDER BY COALESCE(g.order_index, g.name)
            """;
        return jdbc.queryForList(sql, Map.of("yearId", academicYearId));
    }

    // 2) Pass rate per subject for given academic year
    public List<Map<String,Object>> passRateBySubject(Long academicYearId, double passThreshold) {
        String sql = """
            SELECT s.name AS label,
                   ROUND(100.0 * SUM(CASE WHEN r.score >= :pass THEN 1 ELSE 0 END) / NULLIF(COUNT(*),0),2) AS value
            FROM results r
            JOIN subjects s ON s.id = r.subject_id
            WHERE (:yearId IS NULL OR r.academic_year_id = :yearId)
            GROUP BY s.id, s.name
            ORDER BY value DESC
            """;
        return jdbc.queryForList(sql, Map.of("yearId", academicYearId, "pass", passThreshold));
    }

    // 3) Top N students by average score in given year
    public List<Map<String,Object>> topStudents(Long academicYearId, int limit) {
        String sql = """
            SELECT u.id, u.full_name AS label, ROUND(AVG(r.score),2) AS value
            FROM results r
            JOIN users u ON u.id = r.student_id
            WHERE (:yearId IS NULL OR r.academic_year_id = :yearId)
            GROUP BY u.id, u.full_name
            ORDER BY value DESC
            LIMIT :lim
            """;
        return jdbc.queryForList(sql, Map.of("yearId", academicYearId, "lim", limit));
    }

    // 4) Exams created per month (last 12 months) - adjust table/column name if needed
    public List<Map<String,Object>> examsPerMonth(int monthsBack) {
        String sql = """
            SELECT DATE_FORMAT(e.created_at, '%Y-%m') AS label, COUNT(*) AS value
            FROM examinations e
            WHERE e.created_at >= DATE_SUB(CURDATE(), INTERVAL :m MONTH)
            GROUP BY DATE_FORMAT(e.created_at, '%Y-%m')
            ORDER BY label
            """;
        return jdbc.queryForList(sql, Map.of("m", monthsBack));
    }

    // 5) Exams per teacher (top 10)
    public List<Map<String,Object>> examsPerTeacher(int limit) {
        String sql = """
            SELECT u.full_name AS label, COUNT(e.id) AS value
            FROM examinations e
            JOIN users u ON u.id = e.creator_id
            GROUP BY u.id, u.full_name
            ORDER BY value DESC
            LIMIT :lim
            """;
        return jdbc.queryForList(sql, Map.of("lim", limit));
    }
}

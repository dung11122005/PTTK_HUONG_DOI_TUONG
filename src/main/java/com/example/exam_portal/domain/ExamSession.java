package com.example.exam_portal.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "exam_sessions")
@Getter
@Setter
public class ExamSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam; // Đề thi

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassRoom classroom; // Lớp thi

    // Người tạo ca thi: phòng đào tạo (admin user)
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "is_locked")
    private Boolean isLocked = false;

    // Giáo viên bộ môn được phân công chấm/nhập điểm
    // @ManyToMany
    // @JoinTable(
    //     name = "exam_session_teachers",
    //     joinColumns = @JoinColumn(name = "exam_session_id"),
    //     inverseJoinColumns = @JoinColumn(name = "teacher_id")
    // )
    // private Set<User> assignedTeachers = new HashSet<>();
}

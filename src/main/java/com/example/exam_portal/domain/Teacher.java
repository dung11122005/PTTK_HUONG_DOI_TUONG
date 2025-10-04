package com.example.exam_portal.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "teachers")
@Getter
@Setter
public class Teacher {
    @Id
    private Long id; // Trùng với User.id
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    private String teacherCode;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private SubjectDepartment subjectDepartment;


    @ManyToOne
    @JoinColumn(name = "subject_id") // <--- đây sẽ tạo cột subject_id trong bảng teachers
    private Subject subject;
}

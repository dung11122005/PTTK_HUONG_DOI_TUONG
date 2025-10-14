package com.example.exam_portal.domain;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subject_departments")
@Getter
@Setter
public class SubjectDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // "Tổ Toán", "Tổ Văn"

    // Quan hệ 1-1: Mỗi tổ chuyên môn quản lý duy nhất 1 môn
    @OneToOne
    @JoinColumn(name = "subject_id", unique = true, nullable = false)
    private Subject subject;

    // Quan hệ 1-n: Một tổ có nhiều giáo viên
    @OneToMany(mappedBy = "subjectDepartment", cascade = CascadeType.ALL)
    private List<Teacher> teachers;


    public SubjectDepartment(){};

    public SubjectDepartment(String name, Subject subject){
        this.name=name;
        this.subject=subject;
    }
}

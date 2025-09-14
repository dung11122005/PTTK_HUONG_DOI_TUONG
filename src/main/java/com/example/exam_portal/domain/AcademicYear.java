package com.example.exam_portal.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "academic_years")
@Getter 
@Setter
public class AcademicYear {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // "2024-2025"

    private LocalDate startDate;
    private LocalDate endDate;

    private boolean active; // chỉ 1 niên khóa active
}

package com.example.exam_portal.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "teaching_assignments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"class_id", "teacher_id", "subject_id", "role"})
})
@Getter 
@Setter
public class TeachingAssignment {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassRoom classroom;

    private LocalDate startDate;
    private LocalDate endDate;
}

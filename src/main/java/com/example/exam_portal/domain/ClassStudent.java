package com.example.exam_portal.domain;

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
@Table(name = "class_students", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"class_id", "student_id"})
})
@Setter
@Getter
public class ClassStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassRoom classroom;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
}

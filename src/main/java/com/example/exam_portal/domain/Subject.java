package com.example.exam_portal.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subjects")
@Getter 
@Setter
public class Subject {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // "MATH"
    private String name;  // "Toán"

    @OneToMany(mappedBy = "subject")
    private List<Exam> exams;

    @OneToMany(mappedBy = "subject")
    private Set<Teacher> teachers = new HashSet<>();

    public Subject(){};

    public Subject(String code, String name){
        this.code=code;
        this.name=name;
    }

}

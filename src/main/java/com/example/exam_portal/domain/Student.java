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
@Table(name = "students")
@Getter
@Setter
public class Student {
    @Id
    private Long id; // Trùng với User.id
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    private String studentCode;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassRoom classRoom;
}

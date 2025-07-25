package com.example.exam_portal.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class EmailMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime receivedAt;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean replied;

}

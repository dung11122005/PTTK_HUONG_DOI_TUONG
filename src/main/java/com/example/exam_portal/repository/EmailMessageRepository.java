package com.example.exam_portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exam_portal.domain.EmailMessage;


@Repository
public interface EmailMessageRepository extends JpaRepository<EmailMessage, Long>{
    List<EmailMessage> findByRepliedFalseOrderByReceivedAtDesc();
}

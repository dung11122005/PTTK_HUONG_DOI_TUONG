package com.example.exam_portal.service;

import java.util.List;

import com.example.exam_portal.domain.EmailMessage;

public interface  EmailService {
    void sendEmail(String to, String subject, String content);
    
    void sendEmailToMultiple(List<String> recipients, String subject, String content);

    void fetchEmailsFromInbox();

    List<EmailMessage> getAllEmails();
    
    EmailMessage getEmailById(Long id);
}

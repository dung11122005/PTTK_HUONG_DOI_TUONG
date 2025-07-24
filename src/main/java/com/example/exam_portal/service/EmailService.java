package com.example.exam_portal.service;

import java.util.List;

public interface  EmailService {
    void sendEmail(String to, String subject, String content);
    
    void sendEmailToMultiple(List<String> recipients, String subject, String content);
}

package com.example.exam_portal.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.example.exam_portal.domain.EmailMessage;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.repository.EmailMessageRepository;

import jakarta.mail.BodyPart;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailServiceImpl implements EmailService{
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private EmailMessageRepository emailMessageRepository;

    @Autowired
    private UserService userService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.password}")
    private String password;

    @Override
    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(fromEmail);

            helper.setText(content, true); // HTML content

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Gửi mail thất bại", e);
        }
    }

    @Override
    public void sendEmailToMultiple(List<String> recipients, String subject, String content) {
        for (String recipient : recipients) {
            sendEmail(recipient, subject, content);
        }
    }

    // Gửi với template Thymeleaf
    public void sendTemplateEmail(String to, String subject, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        String htmlContent = templateEngine.process("admin/email/email_template", context);

        sendEmail(to, subject, htmlContent);
    }

    @Override
    public void fetchEmailsFromInbox() {
        try {
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");

            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", fromEmail, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();

            // 🔥 Lấy danh sách email của User trong hệ thống
            List<String> validUserEmails = userService.getAllUser()
                    .stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());

            for (Message msg : messages) {
                String from = ((InternetAddress) msg.getFrom()[0]).getAddress();
                String subject = msg.getSubject();
                String content = getTextFromMessage(msg);
                LocalDateTime received = msg.getReceivedDate()
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                // ❌ Bỏ qua nếu người gửi không nằm trong danh sách user
                if (!validUserEmails.contains(from)) continue;

                // ✅ Lưu nếu chưa tồn tại
                if (!isEmailExist(from, subject, received)) {
                    EmailMessage email = new EmailMessage();
                    email.setSender(from);
                    email.setSubject(subject);
                    email.setContent(content);
                    email.setReceivedAt(received);
                    email.setReplied(false);
                    this.emailMessageRepository.save(email);
                }
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isEmailExist(String sender, String subject, LocalDateTime receivedAt) {
        return emailMessageRepository.findAll().stream()
                .anyMatch(e -> e.getSender().equals(sender)
                        && e.getSubject().equals(subject)
                        && e.getReceivedAt().equals(receivedAt));
    }

    private String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) return message.getContent().toString();
        else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) return bodyPart.getContent().toString();
            }
        }
        return "";
    }

    @Override
    public List<EmailMessage> getAllEmails() {
        return emailMessageRepository.findByRepliedFalseOrderByReceivedAtDesc();
    }

    @Override
    public EmailMessage getEmailById(Long id) {
        return emailMessageRepository.findById(id).orElse(null);
    }

    public EmailMessage getSaveEmail(EmailMessage email) {
        return this.emailMessageRepository.save(email);
    }
}

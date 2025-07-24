package com.example.exam_portal.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.exam_portal.domain.ClassStudent;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.service.ClassService;
import com.example.exam_portal.service.EmailServiceImpl;
import com.example.exam_portal.service.UserService;


@Controller
public class MailController {
    
    private final EmailServiceImpl emailService; // dùng EmailServiceImpl vì có phương thức gửi template
    private final ClassService classService;
    private final UserService userService;


    public MailController(EmailServiceImpl emailService, ClassService classService,
    UserService userService){
        this.emailService=emailService;
        this.classService=classService;
        this.userService=userService;
    }

    @GetMapping("/admin/send-mail")
    public String showSendMailForm(Model model, @AuthenticationPrincipal UserDetails userDetails, UserService userService) {
        User teacher = this.userService.getUserByEmail(userDetails.getUsername());
        model.addAttribute("classes", this.classService.getClassByTeacherId(teacher.getId()));
        return "admin/email/sendmail"; // Thymeleaf template
    }

    @PostMapping("/admin/send-mail")
    public String sendMail(@RequestParam(required = false) String email,
                           @RequestParam(required = false) Long classId,
                           @RequestParam String subject,
                           @RequestParam String content,
                           RedirectAttributes redirectAttributes) {

        // Tạo map biến truyền vào Thymeleaf template
        Map<String, Object> variables = new HashMap<>();
        variables.put("message", content); // HTML content (có thể do CKEditor nhập)

        // Gửi cho từng email cá nhân
        if (email != null && !email.isBlank()) {
            emailService.sendTemplateEmail(email, subject, variables);
        }
        // Gửi cho cả lớp
        else if (classId != null) {
            List<ClassStudent> classStudents = this.classService.getClassStudentById(classId);
            for (ClassStudent cs : classStudents) {
                String recipient = cs.getStudent().getEmail();
                emailService.sendTemplateEmail(recipient, subject, variables);
            }
        }

        redirectAttributes.addFlashAttribute("message", "Đã gửi email thành công!");
        return "redirect:/admin/send-mail";
    }
}

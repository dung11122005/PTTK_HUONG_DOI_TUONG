package com.example.exam_portal.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.exam_portal.domain.ClassRoom;
import com.example.exam_portal.domain.ClassStudent;
import com.example.exam_portal.domain.EmailMessage;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.service.ClassService;
import com.example.exam_portal.service.EmailServiceImpl;
import com.example.exam_portal.service.UserService;

import jakarta.validation.Valid;


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

    @PostMapping("/admin/email/send-mail-student")
    public String sendMailStudent(@ModelAttribute("email") @Valid EmailMessage email,
                           @RequestParam String subject,
                           @RequestParam String content,
                           RedirectAttributes redirectAttributes) {

        // Tạo map biến truyền vào Thymeleaf template
        Map<String, Object> variables = new HashMap<>();
        variables.put("message", content); // HTML content (có thể do CKEditor nhập)

        // Gửi cho từng email cá nhân
        if (email.getSender() != null) {
            emailService.sendTemplateEmail(email.getSender(), subject, variables);
        }

        redirectAttributes.addFlashAttribute("message", "Đã gửi email thành công!");
        EmailMessage emaildb=this.emailService.getEmailById(email.getId());
        emaildb.setReplied(true);
        this.emailService.getSaveEmail(emaildb);

        return "redirect:/admin/email";
    }

    @GetMapping("/admin/email/fetch")
    public String fetchEmails(RedirectAttributes redirectAttributes) {
        this.emailService.fetchEmailsFromInbox();
        redirectAttributes.addFlashAttribute("msg", "Đã tải email về!");
        return "redirect:/admin/email";
    }

    @GetMapping("/admin/email")
    public String listEmails(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User teacher = this.userService.getUserByEmail(userDetails.getUsername());
    
        // Lấy các lớp mà giáo viên đang dạy
        List<ClassRoom> classRooms = this.classService.getClassByTeacherId(teacher.getId());
    
        // Lấy tất cả học sinh thuộc các lớp này
        List<User> students = this.classService.getClassStudentByListClass(classRooms);
    
        // Trích danh sách email của học sinh
        Set<String> studentEmails = students.stream()
                .map(User::getEmail)
                .collect(Collectors.toSet());
    
        // Lấy tất cả email phản hồi
        List<EmailMessage> allEmails = this.emailService.getAllEmails();
    
        // Lọc email có email nằm trong danh sách học sinh
        List<EmailMessage> filteredEmails = allEmails.stream()
                .filter(email -> studentEmails.contains(email.getSender()))
                .collect(Collectors.toList());
    
        model.addAttribute("emails", filteredEmails);
        return "admin/email/show";
    }


    @GetMapping("/admin/email/{id}")
    public String viewEmail(@PathVariable Long id, Model model) {
        EmailMessage email = this.emailService.getEmailById(id);
        model.addAttribute("email", email);
        return "admin/email/email_detail";
    }
}

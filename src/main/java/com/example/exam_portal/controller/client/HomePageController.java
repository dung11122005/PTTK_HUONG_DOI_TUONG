package com.example.exam_portal.controller.client;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.exam_portal.domain.Course;
import com.example.exam_portal.domain.Role;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.domain.dto.RegisterDTO;
import com.example.exam_portal.service.CourseService;
import com.example.exam_portal.service.UserService;

import jakarta.validation.Valid;



@Controller
public class HomePageController {

    private final CourseService courseService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public HomePageController(CourseService courseService, UserService userService, PasswordEncoder passwordEncoder){
        this.courseService=courseService;
        this.userService=userService;
        this.passwordEncoder=passwordEncoder;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<Course> courses=this.courseService.getAllCourse();

        model.addAttribute("courses", courses);

        return"client/homepage/show";
    }
    

    @GetMapping("/access-deny")
    public String accessDenied(Model model, Principal principal) {
        return "client/error/403"; // Trỏ đến templates/error/403.html
    }


    @GetMapping("/login")
    public String getLoginPage(Model model) {
        return "client/auth/login";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("newRegister", new RegisterDTO()); 
        return "client/auth/register";
    }

    @PostMapping("/register")
    public String postRegisterPage(Model model, @ModelAttribute("newRegister") @Valid RegisterDTO registerDto,
            BindingResult result) {

        if (!registerDto.isPasswordConfirmed()) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu không khớp");
        }

        if (this.userService.existsByEmail(registerDto.getEmail())) {
            result.rejectValue("email", "error.email", "Email đã tồn tại");
        }

        if (result.hasErrors()) {
            model.addAttribute("newRegister", registerDto);
            return "client/auth/register"; // return form with errors
        }
        User user=new User();
        user.setEmail(registerDto.getEmail());
        String hashPassword = this.passwordEncoder.encode(registerDto.getPassword());

        user.setFullName(registerDto.getFullName());
        user.setPassword(hashPassword);
        user.setPhone(registerDto.getPhone());
        user.setAvatar("default-avatar.jpg");

        // Lấy role STUDENT từ DB
        Role studentRole = this.userService.getRoleByName("STUDENT");
            
        // Set roles (dùng HashSet vì ManyToMany)
        user.setRoles(new HashSet<>(Collections.singletonList(studentRole)));


        this.userService.handleSaveUser(user);
        return "redirect:/login?success";
    }


    @GetMapping("/keep-session-alive")
    @ResponseBody
    public ResponseEntity<String> keepAlive() {
        return ResponseEntity.ok("OK");
    }

}

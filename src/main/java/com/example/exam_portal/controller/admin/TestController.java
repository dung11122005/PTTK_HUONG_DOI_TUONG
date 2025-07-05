package com.example.exam_portal.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exam_portal.domain.ExamSession;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.service.TestService;
import com.example.exam_portal.service.UserService;

@Controller
public class TestController {
    private final TestService testService;
    private final UserService userService;

    public TestController(TestService testService, UserService userService){
        this.testService=testService;
        this.userService=userService;
    }


    @GetMapping("/admin/test")
    public String getTestPage(Model model, @RequestParam("page") Optional<String> pageOptional,
    @AuthenticationPrincipal UserDetails userDetails) {
        User teacher = this.userService.getUserByEmail(userDetails.getUsername());
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            } else {
                page = 1;
            }
        } catch (Exception e) {

        }
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<ExamSession> ex = this.testService.getAllExamSessionPaginationTeacherId(teacher.getId(), pageable);
        List<ExamSession> examSessions =  ex.getContent();
        model.addAttribute("examSessions", examSessions);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ex.getTotalPages());
        return "admin/test/show";
    }
}

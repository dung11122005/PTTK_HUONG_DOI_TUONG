package com.example.exam_portal.controller.admin;

import org.springframework.stereotype.Controller;

import com.example.exam_portal.service.ExamResultService;
import com.example.exam_portal.service.UserService;

@Controller
public class ExamResultController {
    private final ExamResultService examResultService;
    private final UserService userService;

    public ExamResultController(ExamResultService examResultService, UserService userService){
        this.examResultService=examResultService;
        this.userService=userService;
    }

    
}

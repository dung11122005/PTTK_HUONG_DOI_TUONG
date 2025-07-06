package com.example.exam_portal.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.exam_portal.service.CourseService;


@Controller
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService){
        this.courseService=courseService;
    }

    @GetMapping("/course")
    public String getCourseClient(Model model) {
        return "client/course/course";
    }
    
}

package com.example.exam_portal.controller.client;

import org.springframework.stereotype.Controller;

import com.example.exam_portal.service.CourseService;

@Controller
public class CourseClientController {
    private final CourseService courseService;

    public CourseClientController(CourseService courseService){
        this.courseService=courseService;
    }
}

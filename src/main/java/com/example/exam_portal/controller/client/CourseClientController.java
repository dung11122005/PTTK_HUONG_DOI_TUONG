package com.example.exam_portal.controller.client;

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

import com.example.exam_portal.domain.Course;
import com.example.exam_portal.service.CourseService;
import com.example.exam_portal.service.UserService;


@Controller
public class CourseClientController {
    private final CourseService courseService;
    private final UserService userService;

    public CourseClientController(CourseService courseService, UserService userService){
        this.courseService=courseService;
        this.userService=userService;
    }

    @GetMapping("/courses")
    public String getCourseHomePage(Model model, @RequestParam("page") Optional<String> pageOptional,
    @AuthenticationPrincipal UserDetails userDetails) {

        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            } else {
                page = 1;
            }
        } catch (Exception e) {

        }
        Page<Course> us;
        Pageable pageable = PageRequest.of(page - 1, 9);
        
        us = this.courseService.getAllCoursePagination(pageable);
      
        
        List<Course> courses = us.getContent();
        model.addAttribute("courses", courses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", us.getTotalPages());

        return "client/course/course";
    }
    
}

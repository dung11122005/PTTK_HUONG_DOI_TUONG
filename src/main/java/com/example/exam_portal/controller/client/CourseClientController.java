package com.example.exam_portal.controller.client;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exam_portal.domain.Course;
import com.example.exam_portal.domain.CourseLesson;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.service.CourseService;
import com.example.exam_portal.service.PurchaseService;
import com.example.exam_portal.service.UserService;


@Controller
public class CourseClientController {
    private final CourseService courseService;
    private final UserService userService;
    private final PurchaseService purchaseService;

    public CourseClientController(CourseService courseService, UserService userService, PurchaseService purchaseService){
        this.courseService=courseService;
        this.userService=userService;
        this.purchaseService=purchaseService;
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
    
    @GetMapping("/course/detail/{id}")
    public String getCourseDetailtPage(Model model, @PathVariable long id, @AuthenticationPrincipal UserDetails userDetails) {
        Course course=this.courseService.getCourseById(id);
        User user = this.userService.getUserByEmail(userDetails.getUsername());
        int totalLessons = course.getChapters().stream()
            .flatMap(c -> c.getLessons().stream())
            .collect(Collectors.toList())
            .size();

        int totalDuration = course.getChapters().stream()
            .flatMap(c -> c.getLessons().stream())
            .mapToInt(CourseLesson::getDurationMinutes)
            .sum();

        totalDuration=totalDuration/60;

        boolean check=this.purchaseService.checkPurchaseStudentIdAndCourseId(user.getId(), id);
        model.addAttribute("check", check);
        model.addAttribute("course", course);
        model.addAttribute("totalLessons", totalLessons);
        model.addAttribute("totalDuration", totalDuration);
        
        return "client/course/coursedetailt";
    }
    

}

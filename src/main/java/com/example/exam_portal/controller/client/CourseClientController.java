package com.example.exam_portal.controller.client;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exam_portal.domain.Course;
import com.example.exam_portal.domain.CourseLesson;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.service.CourseService;
import com.example.exam_portal.service.GradeService;
import com.example.exam_portal.service.SubjectService;
import com.example.exam_portal.service.UserService;
import com.example.exam_portal.spec.SpecificationBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;



@Controller
public class CourseClientController {
    private final CourseService courseService;
    private final UserService userService;
    private final SubjectService subjectService;
    private final GradeService gradeService;

    public CourseClientController(CourseService courseService, UserService userService, 
    SubjectService subjectService, GradeService gradeService){
        this.courseService=courseService;
        this.userService=userService;
        this.subjectService=subjectService;
        this.gradeService=gradeService;
    }

    @GetMapping("/courses")
    public String getCourseHomePage(Model model,
                                    @RequestParam(name = "page", required = false) Optional<String> pageOptional,
                                    @RequestParam(name = "subject", required = false) List<Long> subjectIds,
                                    @RequestParam(name = "grade", required = false) List<Long> gradeIds,
                                    @RequestParam Map<String, String> params) {
                                    
        int page = pageOptional.map(Integer::parseInt).orElse(1);
        Pageable pageable = PageRequest.of(page - 1, 9);
                                    
        Specification<Course> spec = Specification.where(null);
                                    
        if (subjectIds != null && !subjectIds.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("subject").get("id").in(subjectIds));
        }
    
        if (gradeIds != null && !gradeIds.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("grade").get("id").in(gradeIds));
        }
    
        Page<Course> us = this.courseService.getAllCoursePagination(spec, pageable);
    
        model.addAttribute("courses", us.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", us.getTotalPages());
    
        // Truyền filter ra view
        model.addAttribute("subjects", subjectService.getAllSubject());
        model.addAttribute("grades", gradeService.getAllGrade());
    
        // Quan trọng: truyền lại danh sách đã chọn
        model.addAttribute("subjectIds", subjectIds != null ? subjectIds : List.of());
        model.addAttribute("gradeIds", gradeIds != null ? gradeIds : List.of());
    
        // params nếu muốn giữ lại query string
        model.addAttribute("params", params);
    
        return "client/course/course";
    }




    
    @GetMapping("/course/detail/{id}")
    public String getCourseDetailtPage(Model model, @PathVariable long id, @AuthenticationPrincipal UserDetails userDetails) {
        Course course=this.courseService.getCourseById(id);
        int totalLessons = course.getChapters().stream()
            .flatMap(c -> c.getLessons().stream())
            .collect(Collectors.toList())
            .size();

        int totalDuration = course.getChapters().stream()
            .flatMap(c -> c.getLessons().stream())
            .mapToInt(CourseLesson::getDurationMinutes)
            .sum();

        totalDuration=totalDuration/60;

        model.addAttribute("course", course);
        model.addAttribute("totalLessons", totalLessons);
        model.addAttribute("totalDuration", totalDuration);
        
        return "client/course/coursedetailt";
    }
    
    

    @GetMapping("/test/thank")
    public String getTestThank(Model model) {
        Course course=this.courseService.getCourseById(1);
        model.addAttribute("course", course);
        return "client/thank/thank";
    }
    

    @GetMapping("/test/false")
    public String getTestThankfalse(Model model) {
        Course course=this.courseService.getCourseById(1);
        model.addAttribute("course", course);
        return "client/thank/failure";
    }
    

}

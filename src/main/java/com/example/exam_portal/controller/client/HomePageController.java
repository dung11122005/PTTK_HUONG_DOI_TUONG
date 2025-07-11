package com.example.exam_portal.controller.client;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.exam_portal.domain.Course;
import com.example.exam_portal.service.CourseService;


@Controller
public class HomePageController {

    private final CourseService courseService;

    public HomePageController(CourseService courseService){
        this.courseService=courseService;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<Course> courses=this.courseService.getAllCourse();

        model.addAttribute("courses", courses);

        return"client/homepage/show";
    }
    


    @GetMapping("/login")
    public String getLoginPage(Model model) {
        return "client/auth/login";
    }

    @GetMapping("/keep-session-alive")
    @ResponseBody
    public ResponseEntity<String> keepAlive() {
        return ResponseEntity.ok("OK");
    }

}

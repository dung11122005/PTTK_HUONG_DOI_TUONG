package com.example.exam_portal.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.exam_portal.service.UserService;


@Controller
public class DashboardController {
    private final UserService userService;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    
    @GetMapping("/admin")
    public String getDashboard(Model model) {
        return "admin/dashboard/show";
    }
    
    
}

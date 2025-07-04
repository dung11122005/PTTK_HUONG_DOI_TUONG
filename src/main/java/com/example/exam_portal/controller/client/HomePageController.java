package com.example.exam_portal.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomePageController {

    @GetMapping("/")
    public String getHomePage(Model model) {
        return"client/homepage/show";
    }
    


    @GetMapping("/login")
    public String getLoginPage(Model model) {
        return "client/auth/login";
    }
}

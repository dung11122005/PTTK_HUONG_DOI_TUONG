package com.example.exam_portal.controller.client;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;

@Controller
public class CustomErrorController implements ErrorController{
    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    // @RequestMapping("/error")
    // public String handleError(HttpServletRequest request, Model model) {
    //     WebRequest webRequest = new ServletWebRequest(request, null);
    //     Map<String, Object> errors = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
    //     int status = (int) errors.get("status");
    //     model.addAttribute("status", status);
    //     model.addAttribute("error", errors.get("error"));
    //     model.addAttribute("message", errors.get("message"));

        
    //     return "client/error/404";
        
    // }
}

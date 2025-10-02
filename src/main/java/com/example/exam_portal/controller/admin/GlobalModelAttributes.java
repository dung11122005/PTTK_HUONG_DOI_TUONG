package com.example.exam_portal.controller.admin;

import java.util.Collections;
import java.util.Set;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("permissions")
    public Set<String> addPermissionsToModel(HttpSession session) {
        Set<String> permissions = (Set<String>) session.getAttribute("permissions");
        return permissions != null ? permissions : Collections.emptySet();
    }
}


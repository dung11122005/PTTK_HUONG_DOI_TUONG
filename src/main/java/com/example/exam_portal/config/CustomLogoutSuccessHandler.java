package com.example.exam_portal.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.exam_portal.service.ActivityLogService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private ActivityLogService activityLogService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        if (authentication != null) {
            String username = authentication.getName();
            String ip = request.getRemoteAddr();
            activityLogService.handleSaveActivityLog(username, "Đăng xuất", "POST", "/logout", ip, 200);
        }
        
        response.sendRedirect("/login?logout");
    }
}

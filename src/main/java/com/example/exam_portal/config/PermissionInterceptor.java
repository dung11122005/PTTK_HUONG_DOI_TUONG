package com.example.exam_portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.example.exam_portal.domain.User;
import com.example.exam_portal.service.UserService;
import com.example.exam_portal.util.error.PermissionException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PermissionInterceptor implements HandlerInterceptor{
    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            String email = "";

            if (principal instanceof UserDetails) {
                email = ((UserDetails) principal).getUsername(); // thường là email/username
            } else if (principal instanceof String) {
                email = (String) principal; // trường hợp đơn giản
            }



            if (!email.isEmpty()) {
                User user = userService.getUserByEmail(email);
                if (user != null && user.getRoles() != null) {
                    boolean isAllow = user.getRoles().stream()
                            .filter(r -> r.getPermissions() != null)
                            .flatMap(r -> r.getPermissions().stream())
                            .anyMatch(p -> p.getEndpoint().equals(path)
                                    && p.getMethod().equalsIgnoreCase(httpMethod));

                    if (!isAllow) {
                        System.out.println(">>> BLOCKED: user=" + email + ", method=" + httpMethod + ", path=" + path + ", requestURI=" + requestURI);
                        throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
                    }
                } else {
                    throw new PermissionException("Bạn chưa được gán role nào.");
                }
            }
        }

        return true;
    }
}

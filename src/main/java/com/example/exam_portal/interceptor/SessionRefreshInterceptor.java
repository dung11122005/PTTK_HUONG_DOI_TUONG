package com.example.exam_portal.interceptor;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.exam_portal.domain.Permission;
import com.example.exam_portal.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionRefreshInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("id") != null) {
            Long userId = (Long) session.getAttribute("id");

            // Refresh permission mỗi request
            Set<Permission> permissions = userService.getPermissionsByUserId(userId);
            Set<String> permissionKeys = permissions.stream()
                    .map(p -> p.getMethod() + " " + p.getEndpoint())
                    .collect(Collectors.toSet());

            session.setAttribute("permissions", permissionKeys);
        }
        return true;
    }
}

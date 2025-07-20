package com.example.exam_portal.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.exam_portal.service.ActivityLogService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


// @Component
public class ActivityLogFilter { //extends OncePerRequestFilter
    // private final ActivityLogService activityLogService;

    // public ActivityLogFilter(ActivityLogService activityLogService){
    //     this.activityLogService=activityLogService;
    // }

    // @Override
    // protected void doFilterInternal(HttpServletRequest request,
    //                                 HttpServletResponse response,
    //                                 FilterChain filterChain) throws ServletException, IOException {
    //     String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous";
    //     String path = request.getRequestURI();
    //     String method = request.getMethod();
    //     String ip = request.getRemoteAddr();

    //     filterChain.doFilter(request, response);

    //     int status = response.getStatus();

    //     this.activityLogService.handleSaveActivityLog(username, "Truy cập", method, path, ip, status);
    // }
}

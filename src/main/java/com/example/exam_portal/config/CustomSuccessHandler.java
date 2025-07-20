package com.example.exam_portal.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.exam_portal.domain.User;
import com.example.exam_portal.service.ActivityLogService;
import com.example.exam_portal.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


public class CustomSuccessHandler implements AuthenticationSuccessHandler{
    @Autowired
    private UserService userService;

    @Autowired
    private ActivityLogService activityLogService;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        String targetUrl = determineTargetUrl(authentication);

        // Lấy thông tin username và IP
        String username = authentication.getName();
        String ip = request.getRemoteAddr();

        // Ghi log hoạt động đăng nhập
        this.activityLogService.handleSaveActivityLog(
            username,
            "Đăng nhập thành công",
            "POST",
            request.getRequestURI(), // hoặc "/login"
            ip,
            200
        );

        // Set session attribute nếu cần
        setUserSessionAttributes(request.getSession(false), authentication);

        // Redirect nếu response chưa bị commit
        if (!response.isCommitted()) {
            redirectStrategy.sendRedirect(request, response, targetUrl);
        }
    }


    private String determineTargetUrl(Authentication authentication) {
        Map<String, String> roleTargetUrlMap = new HashMap<>();
        roleTargetUrlMap.put("ROLE_STUDENT", "/");
        roleTargetUrlMap.put("ROLE_ADMIN", "/admin");
        roleTargetUrlMap.put("ROLE_TEACHER", "/admin/exam");

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (roleTargetUrlMap.containsKey(role)) {
                return roleTargetUrlMap.get(role);
            }
        }
        throw new IllegalStateException("User role not recognized");
    }

    private void setUserSessionAttributes(HttpSession session, Authentication authentication) {
        if (session == null) return;

        // Xóa lỗi xác thực cũ (nếu có)
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        if (user != null) {
            session.setAttribute("fullName", user.getFullName());
            session.setAttribute("avatar", user.getAvatar());
            session.setAttribute("address", user.getAddress());
            session.setAttribute("phone", user.getPhone());
            session.setAttribute("id", user.getId());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("role", user.getRole().getName());
        }
    }
}

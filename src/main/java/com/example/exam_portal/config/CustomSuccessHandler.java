package com.example.exam_portal.config;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.exam_portal.domain.Permission;
import com.example.exam_portal.domain.Role;
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
        // Map<String, String> roleTargetUrlMap = new HashMap<>();
        // roleTargetUrlMap.put("ROLE_STUDENT", "/");
        // roleTargetUrlMap.put("ROLE_ADMIN", "/admin/user");          // Quản trị viên hệ thống
        // roleTargetUrlMap.put("ROLE_PRINCIPAL", "/admin");           // Hiệu trưởng → Dashboard
        // roleTargetUrlMap.put("ROLE_VICE_PRINCIPAL", "/admin/test");           // Hiệu phó → test
        // roleTargetUrlMap.put("ROLE_SUBJECT_DEPARTMENT", "/admin/exam"); // Tổ bộ môn
        // roleTargetUrlMap.put("ROLE_SUBJECT_TEACHER", "/admin/exam");   // Giáo viên bộ môn → Quản lý kỳ thi
        // roleTargetUrlMap.put("ROLE_HOMEROOM_TEACHER", "/admin/class"); // Giáo viên chủ nhiệm → Quản lý lớp

        // for (GrantedAuthority authority : authentication.getAuthorities()) {
        //     String role = authority.getAuthority();
        //     if (roleTargetUrlMap.containsKey(role)) {
        //         return roleTargetUrlMap.get(role);
        //     }
        // }

        return "/";
        // throw new IllegalStateException("User role not recognized: " + authentication.getAuthorities());
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
                
            // Lấy tất cả role name
            Set<String> roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
                
            session.setAttribute("roles", roleNames); // Lưu dưới dạng Set

             // Lấy tất cả permission (bao gồm method + url)
            Set<Permission> permissions = this.userService.getPermissionsByUserId(user.getId());
            
            // Chuyển thành set String cho dễ check ở view
            Set<String> permissionKeys = permissions.stream()
                    .map(p -> p.getMethod() + " " + p.getEndpoint())  // ví dụ: "GET /admin/user"
                    .collect(Collectors.toSet());
           

            session.setAttribute("permissions", permissionKeys);
        }

    }
}

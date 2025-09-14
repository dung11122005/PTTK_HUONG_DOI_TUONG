package com.example.exam_portal.controller.admin;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.exam_portal.domain.Role;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.service.ActivityLogService;
import com.example.exam_portal.service.UploadService;
import com.example.exam_portal.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class UserController {
    
    private final UserService userService;
    private  final UploadService uploadService;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;

    public UserController(UserService userService, 
        UploadService uploadService,
        PasswordEncoder passwordEncoder,
        ActivityLogService activityLogService){
        this.userService=userService;
        this.uploadService=uploadService;
        this.passwordEncoder=passwordEncoder;
        this.activityLogService=activityLogService;
    }


    @RequestMapping("/admin/user")
    public String getUserPage(Model model, @RequestParam("page") Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            } else {
                page = 1;
            }
        } catch (Exception e) {

        }
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<User> us = this.userService.getAllUserPagination(pageable);
        List<User> users = us.getContent();
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", us.getTotalPages());
        return "admin/user/show";
    }

    @GetMapping("/admin/user/create")
    public String getCreateUserPage(Model model) {
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }

    @PostMapping(value = "/admin/user/create")
    public String createUserPage(Model model,
            @ModelAttribute("newUser") @Valid User hoidanit,
            BindingResult newUserBindingResult,
            @RequestParam("anhFile") MultipartFile file,
            @RequestParam(value = "roleIds", required = false) List<Long> roleIds,  // nhận nhiều role
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {

        if (newUserBindingResult.hasErrors()) {
            return "admin/user/create";
        }

        // Upload avatar
        String avatar = this.uploadService.handleSaveUploadFile(file, "avatars");
        String hashPassword = this.passwordEncoder.encode(hoidanit.getPassword());

        hoidanit.setAvatar(avatar);
        hoidanit.setPassword(hashPassword);

        // Lấy danh sách role từ DB theo roleIds
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = new HashSet<>(this.userService.getRolesByIds(roleIds));
            hoidanit.setRoles(roles);
        }

        // ActivityLog
        String ip = request.getRemoteAddr();
        activityLogService.handleSaveActivityLog(
                userDetails.getUsername(),
                "Tạo user",
                "POST",
                "/admin/user/create",
                ip,
                200
        );

        // Save user
        this.userService.handleSaveUser(hoidanit);
        
        return "redirect:/admin/user";
    }


    @RequestMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable long id) {
        User currentUser = this.userService.getUserById(id);
        model.addAttribute("newUser", currentUser);
        return "admin/user/detail";
    }

    @RequestMapping("/admin/user/update/{id}")
    public String getUpdateUserPage(Model model, @PathVariable long id) {
        User currentUser = this.userService.getUserById(id);
        model.addAttribute("newUser", currentUser);
        return "admin/user/update";
    }

    @PostMapping("/admin/user/update")
    public String postUpdateUser(@ModelAttribute("newUser") @Valid User hoidanit,
            BindingResult newUserBindingResult,
            @RequestParam("anhFile") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {
        User currentUser = this.userService.getUserById(hoidanit.getId());
        if (currentUser != null) {
            // update new image
            if (!file.isEmpty()) {
                String img = this.uploadService.handleSaveUploadFile(file, "avatars");
                currentUser.setAvatar(img);
            }
            currentUser.setFullName(hoidanit.getFullName());
            currentUser.setAddress(hoidanit.getAddress());
            currentUser.setPhone(hoidanit.getPhone());
            
            String ip = request.getRemoteAddr();
            this.activityLogService.handleSaveActivityLog(userDetails.getUsername(), "Sửa user", "POST", "/admin/user/update/" + hoidanit.getId(), ip, 200);
            this.userService.handleSaveUser(currentUser);
        }
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        return "admin/user/delete";
    }

    @PostMapping("/admin/user/delete")
    public String postDeleteUser(@RequestParam("id") Long id, @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        this.activityLogService.handleSaveActivityLog(userDetails.getUsername(), "Xóa user", "POST", "/admin/user/delete/" + id, ip, 200);
        this.userService.deleteAUser(id);
        return "redirect:/admin/user";
    }
}

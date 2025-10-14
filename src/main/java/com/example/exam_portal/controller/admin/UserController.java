package com.example.exam_portal.controller.admin;

import java.util.HashSet;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.exam_portal.domain.Role;
import com.example.exam_portal.domain.Teacher;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.repository.RoleRepository;
import com.example.exam_portal.service.ActivityLogService;
import com.example.exam_portal.service.StudentService;
import com.example.exam_portal.service.SubjectDepartmentService;
import com.example.exam_portal.service.SubjectService;
import com.example.exam_portal.service.TeacherService;
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
    private final RoleRepository roleRepository;
    private final SubjectService subjectService;
    private final SubjectDepartmentService subjectDepartmentService;
    private final TeacherService teacherService;
    private final StudentService studentService;

    public UserController(UserService userService, 
        UploadService uploadService,
        PasswordEncoder passwordEncoder,
        ActivityLogService activityLogService, RoleRepository roleRepository,
        SubjectService subjectService, SubjectDepartmentService subjectDepartmentService,
        TeacherService teacherService, StudentService studentService){
        this.userService=userService;
        this.uploadService=uploadService;
        this.passwordEncoder=passwordEncoder;
        this.activityLogService=activityLogService;
        this.roleRepository=roleRepository;
        this.subjectService=subjectService;
        this.subjectDepartmentService=subjectDepartmentService;
        this.teacherService=teacherService;
        this.studentService=studentService;
    }


    @GetMapping("/admin/user")
    public String getUserGroupsPage(Model model) {
        // Tạo 2 nhóm:
        // 1. STUDENT
        // 2. Staff (gộp tất cả role còn lại)
        List<String> groups = List.of("STUDENT", "STAFF");
        model.addAttribute("groups", groups);
        return "admin/user/index";
    }

    @GetMapping("/admin/user/group/{groupName}")
    public String getUsersByGroup(@PathVariable("groupName") String groupName,
                                  Model model,
                                  @RequestParam(value = "page", defaultValue = "1") int page) {
        Pageable pageable = PageRequest.of(page - 1, 12);
        Page<User> users;

        if ("STUDENT".equals(groupName)) {
            users = userService.getUsersByRole("STUDENT", pageable);
        } else { // STAFF
            List<String> staffRoles = List.of(
                "ACADEMIC_AFFAIRS_OFFICE", "PRINCIPAL", "VICE_PRINCIPAL",
                "SUBJECT_DEPARTMENT", "SUBJECT_TEACHER", "HOMEROOM_TEACHER"
            );
            users = userService.getUsersByRoles(staffRoles, pageable);
        }

        model.addAttribute("groupName", groupName);
        model.addAttribute("users", users.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());

        return "admin/user/show";
    }


    @GetMapping("/admin/user/create")
    public String getCreateUserPage(Model model) {
        model.addAttribute("newUser", new User());
        model.addAttribute("allRoles", this.roleRepository.findAll());
        return "admin/user/create";
    }


    @PostMapping("/admin/user/create")
    public String createUserPage(Model model,
            @ModelAttribute("newUser") @Valid User hoidanit,
            BindingResult newUserBindingResult,
            @RequestParam("anhFile") MultipartFile file,
            @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {

        if (newUserBindingResult.hasErrors()) {
            // load lại allRoles để hiển thị lại form khi có lỗi
            model.addAttribute("allRoles", this.userService.getAllRoles());
            return "admin/user/create";
        }

        // Upload avatar
        String avatar = this.uploadService.handleSaveUploadFile(file, "avatars");
        String hashPassword = this.passwordEncoder.encode(hoidanit.getPassword());

        hoidanit.setAvatar(avatar);
        hoidanit.setPassword(hashPassword);

        // Lấy danh sách role từ DB theo roleIds
        Set<Role> roles = new HashSet<>();
        if (roleIds != null && !roleIds.isEmpty()) {
            roles.addAll(this.userService.getRolesByIds(roleIds));
            hoidanit.setRoles(roles);
        }

        // Nếu role là SUBJECT_TEACHER thì tạo Teacher entity
        boolean isTeacher = roles.stream()
                .anyMatch(r -> "SUBJECT_TEACHER".equals(r.getName()));

        if (isTeacher) {
            Teacher teacher = new Teacher();
            teacher.setUser(hoidanit);
            // teacherCode có thể sinh tự động
            teacher.setTeacherCode("TCH" + System.currentTimeMillis());
            // Chưa gán department hay subject
            hoidanit.setTeacher(teacher);
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

        // Save user (cascade sẽ lưu luôn Teacher)
        this.userService.handleSaveUser(hoidanit);

        return "redirect:/admin/user";
    }



    @GetMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable long id) {
        User currentUser = this.userService.getUserById(id);
        model.addAttribute("newUser", currentUser);
        model.addAttribute("allRoles", this.roleRepository.findAll());
        return "admin/user/detail";
    }

    @GetMapping("/admin/user/update/{id}")
    public String getUpdateUserPage(Model model, @PathVariable long id) {
        User currentUser = this.userService.getUserById(id);
        model.addAttribute("newUser", currentUser);
        model.addAttribute("allRoles", this.roleRepository.findAll());
        return "admin/user/update";
    }

    @PostMapping("/admin/user/update")
    public String postUpdateUser(
            @ModelAttribute("newUser") @Valid User hoidanit,
            BindingResult newUserBindingResult,
            @RequestParam("anhFile") MultipartFile file,
            @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {

        User currentUser = this.userService.getUserById(hoidanit.getId());
        if (currentUser != null) {
            // update avatar
            if (!file.isEmpty()) {
                String img = this.uploadService.handleSaveUploadFile(file, "avatars");
                currentUser.setAvatar(img);
            }

            currentUser.setEmail(hoidanit.getEmail());
            currentUser.setFullName(hoidanit.getFullName());
            currentUser.setAddress(hoidanit.getAddress());
            currentUser.setPhone(hoidanit.getPhone());

            // cập nhật roles
            if (roleIds != null && !roleIds.isEmpty()) {
                Set<Role> roles = new HashSet<>(this.userService.getRolesByIds(roleIds));
                currentUser.setRoles(roles);
            } else {
                currentUser.setRoles(new HashSet<>()); // clear hết nếu không chọn gì
            }

            // Activity log
            String ip = request.getRemoteAddr();
            this.activityLogService.handleSaveActivityLog(
                    userDetails.getUsername(),
                    "Sửa user",
                    "POST",
                    "/admin/user/update/" + hoidanit.getId(),
                    ip,
                    200
            );

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

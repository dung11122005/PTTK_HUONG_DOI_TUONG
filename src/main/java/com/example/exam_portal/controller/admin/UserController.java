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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.exam_portal.domain.Role;
import com.example.exam_portal.domain.Student;
import com.example.exam_portal.domain.Subject;
import com.example.exam_portal.domain.SubjectDepartment;
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
        model.addAttribute("allRoles", this.roleRepository.findAll());

        model.addAttribute("allSubjects", this.subjectService.getAllSubject());
        model.addAttribute("allDepartments", this.subjectDepartmentService.getAllSubjectDepartment());
        return "admin/user/create";
    }

    @PostMapping("/admin/user/create")
    public String createUserPage(Model model,
            @ModelAttribute("newUser") @Valid User hoidanit,
            BindingResult newUserBindingResult,
            @RequestParam(value = "anhFile", required = false) MultipartFile file,
            @RequestParam(value = "selectedRoleIds", required = false) List<Long> roleIds,
            @RequestParam(value = "teacherCode", required = false) String teacherCode,
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "subjectId", required = false) Long subjectId, // CHỈ 1 MÔN
            @RequestParam(value = "studentCode", required = false) String studentCode,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {

        if (newUserBindingResult.hasErrors()) {
            model.addAttribute("allRoles", this.userService.getAllRoles());
            model.addAttribute("allSubjects", this.subjectService.getAllSubject());
            model.addAttribute("allDepartments", this.subjectDepartmentService.getAllSubjectDepartment());
            return "admin/user/create";
        }

        // Upload avatar + encode password
        String avatar = this.uploadService.handleSaveUploadFile(file, "avatars");
        hoidanit.setAvatar(avatar);
        hoidanit.setPassword(passwordEncoder.encode(hoidanit.getPassword()));

        // Gán roles nếu có
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = new HashSet<>(this.userService.getRolesByIds(roleIds));
            hoidanit.setRoles(roles);
        }

        // Save user trước
        User savedUser = this.userService.handleSaveUser(hoidanit);

        // Tạo Teacher nếu có role TEACHER
        if (savedUser.getRoles().stream().anyMatch(r -> "SUBJECT_TEACHER".equalsIgnoreCase(r.getName()))) {
            Teacher teacher = new Teacher();
            teacher.setUser(savedUser);
            teacher.setTeacherCode(teacherCode);

            if (departmentId != null) {
                SubjectDepartment dept = this.subjectDepartmentService.getSubjectDepartmentById(departmentId).orElse(null);
                teacher.setSubjectDepartment(dept);
            }

            if (subjectId != null) {
                Subject subject = this.subjectService.getSubjectById(subjectId).orElse(null);
                teacher.getSubjects().add(subject); // vẫn dùng Set nhưng chỉ add 1
            }

            this.teacherService.handleSaveTeacher(teacher);
        }

        // Tạo Student nếu có role STUDENT
        if (savedUser.getRoles().stream().anyMatch(r -> "STUDENT".equalsIgnoreCase(r.getName()))) {
            Student student = new Student();
            student.setUser(savedUser);
            student.setStudentCode(studentCode);
            this.studentService.handleSaveStudent(student);
        }

        // Activity log
        String ip = request.getRemoteAddr();
        activityLogService.handleSaveActivityLog(
                userDetails.getUsername(),
                "Tạo user",
                "POST",
                "/admin/user/create",
                ip,
                200
        );

        return "redirect:/admin/user";
    }





    @GetMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable long id) {
        User currentUser = this.userService.getUserById(id);
        model.addAttribute("newUser", currentUser);
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

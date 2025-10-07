package com.example.exam_portal.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exam_portal.domain.Teacher;
import com.example.exam_portal.service.SubjectDepartmentService;
import com.example.exam_portal.service.SubjectService;
import com.example.exam_portal.service.TeacherService;



@Controller
public class TeacherController {
    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final SubjectDepartmentService subjectDepartmentService;

    public TeacherController(TeacherService teacherService, SubjectService subjectService,
    SubjectDepartmentService subjectDepartmentService){
        this.teacherService=teacherService;
        this.subjectService=subjectService;
        this.subjectDepartmentService=subjectDepartmentService;
    }

    @GetMapping("/admin/teacher")
    public String getTeacherPage(Model model, @RequestParam("page") Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {
            page = 1;
        }
    
        Pageable pageable = PageRequest.of(page - 1, 12);
        Page<Teacher> tes = this.teacherService.getAllTeacherPagination(pageable);
        List<Teacher> teachers = tes.getContent();
    
        model.addAttribute("teachers", teachers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tes.getTotalPages());
        return "admin/teacher/show";
    }


    @GetMapping("/admin/teacher/update/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Teacher teacher = teacherService.getTeacherById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giáo viên id: " + id));

        model.addAttribute("teacher", teacher);
        model.addAttribute("departments", this.subjectDepartmentService.getAllSubjectDepartment());
        model.addAttribute("subjects", this.subjectService.getAllSubject());

        return "admin/teacher/update";
    }

    // Xử lý submit
    @PostMapping("/admin/teacher/update/{id}")
    public String updateTeacher(
            @PathVariable("id") Long id,
            @ModelAttribute("teacher") Teacher updatedTeacher
    ) {
        Teacher teacher = teacherService.getTeacherById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giáo viên id: " + id));

        // Chỉ cập nhật tổ môn và môn
        teacher.setSubjectDepartment(updatedTeacher.getSubjectDepartment());
        teacher.setSubject(updatedTeacher.getSubject());

        teacherService.handleSaveTeacher(teacher);

        return "redirect:/admin/teacher";
    }
}

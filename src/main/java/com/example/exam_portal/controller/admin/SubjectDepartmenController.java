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

import com.example.exam_portal.domain.SubjectDepartment;
import com.example.exam_portal.service.SubjectDepartmentService;
import com.example.exam_portal.service.SubjectService;
import com.example.exam_portal.service.TeacherService;



@Controller
public class SubjectDepartmenController {
    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final SubjectDepartmentService subjectDepartmentService;

    public SubjectDepartmenController(TeacherService teacherService, SubjectService subjectService,
    SubjectDepartmentService subjectDepartmentService){
        this.teacherService=teacherService;
        this.subjectService=subjectService;
        this.subjectDepartmentService=subjectDepartmentService;
    }

    @GetMapping("/admin/subject-department")
    public String getDepartmentPage(Model model, @RequestParam("page") Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {
            page = 1;
        }
    
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<SubjectDepartment> sub = this.subjectDepartmentService.getAllSubjectDepartmentPagination(pageable);
        List<SubjectDepartment> department = sub.getContent();
    
        model.addAttribute("departments", department);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sub.getTotalPages());
        return "admin/department/show";
    }


    @GetMapping("/admin/subject-department/create")
    public String getCreateSubjectDepartment(Model model) {
        model.addAttribute("newSubjectDepartment", new SubjectDepartment());
        model.addAttribute("newSubject", this.subjectService.getAllSubject());
        return "admin/department/create";
    }

    @PostMapping("/admin/subject-department/create")
    public String createSubjectDepartment(@ModelAttribute("newSubjectDepartment") SubjectDepartment department) {
        this.subjectDepartmentService.handleSaveSubjectDepartment(department);
        return "redirect:/admin/subject-department";
    }


    @GetMapping("/admin/subject-department/update/{id}")
    public String getUpdateSubjectDepartmentPage(Model model, @PathVariable Long id) {
        Optional<SubjectDepartment> sdOpt = this.subjectDepartmentService.getSubjectDepartmentById(id);
        if(sdOpt.isEmpty()) {
            return "redirect:/admin/subject-department?error=notfound";
        }
        model.addAttribute("newSubjectDepartment", sdOpt.get());
        return "admin/department/update";
    }
    
    @PostMapping("/admin/subject-department/update/{id}")
    public String updateSubjectDepartment(
            @PathVariable Long id,
            @ModelAttribute("newSubjectDepartment") SubjectDepartment updatedSubjectDepartment
    ) {
        Optional<SubjectDepartment> exOpt = this.subjectDepartmentService.getSubjectDepartmentById(id);
        if(exOpt.isPresent()) {
            SubjectDepartment existing = exOpt.get();
            existing.setName(updatedSubjectDepartment.getName()); // chỉ cập nhật tên tổ
            this.subjectDepartmentService.handleSaveSubjectDepartment(existing);
        }
        return "redirect:/admin/subject-department";
    }



    @GetMapping("/admin/subject-department/delete/{id}")
    public String getDeleteSubjectDepartmentPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        return "admin/department/delete";
    }
    
    @PostMapping("/admin/subject-department/delete")
    public String postDeleteSubjectDepartment(@RequestParam("id") Long id) {
        this.subjectDepartmentService.deleteSubjectDepartment(id);
        return "redirect:/admin/subject-department";
    }
}

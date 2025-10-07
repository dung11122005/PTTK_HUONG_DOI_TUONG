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

import com.example.exam_portal.domain.Subject;
import com.example.exam_portal.service.SubjectService;


@Controller
public class SubjectController {
    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService){
        this.subjectService=subjectService;
    }

    @GetMapping("/admin/subject")
    public String getSubjectPage(Model model, @RequestParam("page") Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {
            page = 1;
        }
    
        Pageable pageable = PageRequest.of(page - 1, 12);
        Page<Subject> sub = this.subjectService.getAllSubjectPagination(pageable);
        List<Subject> subjects = sub.getContent();
    
        model.addAttribute("subjects", subjects);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sub.getTotalPages());
        return "admin/subject/show";
    }
    
    @GetMapping("/admin/subject/create")
    public String getCreateSubjectPage(Model model) {
        model.addAttribute("newSubject", new Subject());
        return "admin/subject/create";
    }
    
    @PostMapping("/admin/subject/create")
    public String createSubject(@ModelAttribute("newSubject") Subject subject) {
        this.subjectService.handleSaveSubject(subject);
        return "redirect:/admin/subject";
    }
    
    @GetMapping("/admin/subject/update/{id}")
    public String getUpdateSubjectPage(Model model, @PathVariable long id) {
        Optional<Subject> subject = this.subjectService.getSubjectById(id);
        model.addAttribute("newSubject", subject.get());
        return "admin/subject/update";
    }
    
    @PostMapping("/admin/subject/update/{id}")
    public String updateSubject(@PathVariable Long id, @ModelAttribute("newSubject") Subject updatedSubject) {
        Optional<Subject> ex = this.subjectService.getSubjectById(id);
        if (ex.isPresent()) {
            Subject existing = ex.get();
            existing.setCode(updatedSubject.getCode());
            existing.setName(updatedSubject.getName());
            this.subjectService.handleSaveSubject(existing);
        }
        return "redirect:/admin/subject";
    }
    
    @GetMapping("/admin/subject/delete/{id}")
    public String getDeleteSubjectPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        return "admin/subject/delete";
    }
    
    @PostMapping("/admin/subject/delete")
    public String postDeleteSubject(@RequestParam("id") Long id) {
        this.subjectService.deleteSubject(id);
        return "redirect:/admin/subject";
    }


}

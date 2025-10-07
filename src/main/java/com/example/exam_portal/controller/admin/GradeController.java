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

import com.example.exam_portal.domain.Grade;
import com.example.exam_portal.service.GradeService;

@Controller
public class GradeController {
    private final GradeService gradeService;

    public GradeController(GradeService gradeService){
        this.gradeService=gradeService;
    }

    @GetMapping("/admin/grade")
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
        Pageable pageable = PageRequest.of(page - 1, 12);
        Page<Grade> gr = this.gradeService.getAllGradePagination(pageable);
        List<Grade> grades = gr.getContent();
        model.addAttribute("grades", grades);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", gr.getTotalPages());
        return "admin/grade/show";
    }

    @GetMapping("/admin/grade/create")
    public String getCreateGrade(Model model) {
        model.addAttribute("newGrade", new Grade());
        return "admin/grade/create";
    }

    @PostMapping("/admin/grade/create")
    public String createGrade(@ModelAttribute("newAcademicYear") Grade grade) {

        this.gradeService.handleSaveGrade(grade);
        return "redirect:/admin/grade";
    }

    @GetMapping("/admin/grade/update/{id}")
    public String getUpdateGradePage(Model model, @PathVariable long id) {
        Optional<Grade> grade = this.gradeService.getGradeById(id);
        model.addAttribute("newGrade", grade.get());
        return "admin/grade/update";
    }

    @PostMapping("/admin/grade/update/{id}")
    public String updateGrade(
            @PathVariable Long id,
            @ModelAttribute("newGrade") Grade updatedGrade
    ) {
        Optional<Grade> ex = this.gradeService.getGradeById(id);
        if (ex.isPresent()) {
            Grade existing = ex.get();
            existing.setName(updatedGrade.getName());
            existing.setOrderIndex(updatedGrade.getOrderIndex());

            this.gradeService.handleSaveGrade(existing);
        }
        return "redirect:/admin/grade";
    }


    @GetMapping("/admin/grade/delete/{id}")
    public String getDeleteGradePage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        return "admin/grade/delete";
    }
    
    @PostMapping("/admin/grade/delete")
    public String postDeleteGrade(@RequestParam("id") Long id) {
        this.gradeService.deleteGrade(id);
        return "redirect:/admin/grade";
    }


}

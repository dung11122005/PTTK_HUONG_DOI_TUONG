package com.example.exam_portal.controller.admin;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exam_portal.domain.Exam;
import com.example.exam_portal.service.ExamService;


@Controller
public class ExamController {
    

    private final ExamService examService;

    public ExamController(ExamService examService){
        this.examService=examService;
    }

    @GetMapping("/admin/exam")
    public String getExamPage(Model model, @RequestParam("page") Optional<String> pageOptional) {
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
        Page<Exam> us = this.examService.getAllExamPagination(pageable);
        List<Exam> exams = us.getContent();
        model.addAttribute("exams", exams);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", us.getTotalPages());
        return "admin/exam/show";
    }
    

}

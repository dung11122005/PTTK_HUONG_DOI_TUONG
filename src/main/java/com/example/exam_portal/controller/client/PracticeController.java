package com.example.exam_portal.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exam_portal.domain.FeedbackResult;
import com.example.exam_portal.service.PracticeService;


@Controller
@RequestMapping("/practice")
public class PracticeController {
    @Autowired
    private PracticeService practiceService;

    @GetMapping
    public String getPracticePage(Model model) {
        String sentence = practiceService.generateRandomVietnameseSentence();
        model.addAttribute("sourceVi", sentence);
        return "client/chatbot/practice";
    }

    @PostMapping("/submit")
    public String submitTranslation(
            @RequestParam String sourceVi,
            @RequestParam String studentEn,
            Model model) {

        FeedbackResult result = practiceService.analyzeStudentTranslation(sourceVi, studentEn);
        
        model.addAttribute("sourceVi", sourceVi);
        model.addAttribute("studentEn", studentEn);
        model.addAttribute("result", result);
        return "client/chatbot/practice";
    }
}

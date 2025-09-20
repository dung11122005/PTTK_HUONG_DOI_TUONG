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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exam_portal.domain.Choice;
import com.example.exam_portal.domain.Exam;
import com.example.exam_portal.domain.Grade;
import com.example.exam_portal.domain.Question;
import com.example.exam_portal.domain.Subject;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.domain.enums.QuestionType;
import com.example.exam_portal.service.ExamService;
import com.example.exam_portal.service.GradeService;
import com.example.exam_portal.service.QuestionService;
import com.example.exam_portal.service.SubjectService;
import com.example.exam_portal.service.UserService;


@Controller
public class ExamController {

    private final ExamService examService;
    private  final UserService userService;
    private final QuestionService questionService;
    private final SubjectService subjectService;
    private final GradeService gradeService;


    public ExamController(ExamService examService, UserService userService, 
    QuestionService questionService, SubjectService subjectService, GradeService gradeService){
        this.examService=examService;
        this.userService=userService;
        this.questionService=questionService;
        this.subjectService=subjectService;
        this.gradeService=gradeService;
    }

    @GetMapping("/admin/exam")
    public String getExamPage(Model model, @RequestParam("page") Optional<String> pageOptional,
    @AuthenticationPrincipal UserDetails userDetails) {
        User teacher = this.userService.getUserByEmail(userDetails.getUsername());
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            } else {
                page = 1;
            }
        } catch (Exception e) {

        }
        Page<Exam> us;
        Pageable pageable = PageRequest.of(page - 1, 10);
        boolean isAdmin = teacher.getRoles().stream()
        .anyMatch(role -> role.getName().equalsIgnoreCase("PRINCIPAL"));
        if(isAdmin){
            us = this.examService.getAllExamPagination(pageable);
        }else{
            us = this.examService.getAllExamPaginationTeacherId(teacher.getId(), pageable);
        }
        
        List<Exam> exams = us.getContent();
        model.addAttribute("exams", exams);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", us.getTotalPages());
        return "admin/exam/show";
    }
    
    @GetMapping("/admin/exam/create")
    public String getCreateExamPage(Model model) {
        model.addAttribute("newExam", new Exam());
        model.addAttribute("subjects", this.subjectService.getAllSubject());
        model.addAttribute("grades", this.gradeService.getAllGrade()); // thêm khối
        return "admin/exam/create";
    }

    @PostMapping("/admin/exam/create")
    public String postCreateExam(@ModelAttribute("newExam") Exam form,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        User teacher = this.userService.getUserByEmail(userDetails.getUsername());

        Exam exam = new Exam();
        exam.setName(form.getName());
        exam.setDescription(form.getDescription());
        exam.setTimeLimit(form.getTimeLimit());
        exam.setIsPublic(form.getIsPublic());

        // gán môn học
        Optional<Subject> subject = this.subjectService.getSubjectById(form.getSubject().getId());
        exam.setSubject(subject.get());

        // gán khối
        Optional<Grade> grade = this.gradeService.getGradeById(form.getGrade().getId());
        exam.setGrade(grade.get());

        // gán người tạo
        exam.setCreatedBy(teacher);

        this.examService.handleSaveExam(exam);
        return "redirect:/admin/exam";
    }

    
    

    @GetMapping("/admin/exam/update/{id}")
    public String getUpdateExamPage(Model model, @PathVariable Long id) {
        Exam exam = this.examService.getExamById(id);
        model.addAttribute("newExam", exam);
        model.addAttribute("subjects", this.subjectService.getAllSubject());
        model.addAttribute("grades", this.gradeService.getAllGrade());
        return "admin/exam/update";
    }

    @PostMapping("/admin/exam/update/{id}")
    public String postUpdateExam(@PathVariable Long id,
                                 @ModelAttribute("newExam") Exam form,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        User teacher = this.userService.getUserByEmail(userDetails.getUsername());

        Exam exam = this.examService.getExamById(id);
        exam.setName(form.getName());
        exam.setDescription(form.getDescription());
        exam.setTimeLimit(form.getTimeLimit());
        exam.setIsPublic(form.getIsPublic());

        // cập nhật subject
        Optional<Subject> subject = subjectService.getSubjectById(form.getSubject().getId());
        exam.setSubject(subject.get());

        // cập nhật grade
        Optional<Grade> grade = gradeService.getGradeById(form.getGrade().getId());
        exam.setGrade(grade.get());

        // vẫn giữ nguyên createdBy (hoặc nếu muốn thì gán lại)
        exam.setCreatedBy(teacher);

        this.examService.handleSaveExam(exam);
        return "redirect:/admin/exam";
    }


    @GetMapping("/admin/exam/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        return "admin/exam/delete";
    }

    @PostMapping("/admin/exam/delete")
    public String postDeleteUser(@RequestParam("id") Long id) {
        this.examService.deleteAExam(id);
        return "redirect:/admin/exam";
    }

    @GetMapping("/admin/exam/{id}")
    public String getQuestionsExam(@PathVariable Long id, Model model) {
        Exam exam = this.examService.getExamById(id);
        List<Question> questions = this.questionService.getQuestionsByExamId(id);
        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        return "admin/question/create";
    }

    @PostMapping("/admin/exam/question/create/{id}")
    public String postCreateQuestion(@PathVariable Long id,
                              @RequestParam QuestionType type,
                              @RequestParam String content,
                              @RequestParam(required = false) String imageUrl,
                              @RequestParam Float points,
                              @RequestParam(required = false) String correctAnswer,
                              @RequestParam(required = false) List<String> choicesContent,
                              @RequestParam(required = false) List<String> isCorrect) {

        Exam exam = this.examService.getExamById(id);
        Question q = new Question();
        q.setExam(exam);
        q.setType(type);
        q.setContent(content);
        q.setPoints(points);

        if (type == QuestionType.FILL_IN_BLANK) {
            q.setCorrectAnswer(correctAnswer);
        }

        if (type != QuestionType.FILL_IN_BLANK && choicesContent != null) {
            Set<Integer> correctIndexes = new HashSet<>();
            if (isCorrect != null) {
                for (String s : isCorrect) {
                    try {
                        correctIndexes.add(Integer.parseInt(s));
                    } catch (NumberFormatException ignored) {}
                }
            }

            for (int i = 0; i < choicesContent.size(); i++) {
                Choice c = new Choice();
                c.setQuestion(q);
                c.setContent(choicesContent.get(i));
                c.setIsCorrect(correctIndexes.contains(i));
                q.getChoices().add(c);
            }
        }

        this.questionService.handleSaveQuestion(q);
        return "redirect:/admin/exam/{id}";
    }

    @PostMapping("/admin/exam/question/update/{id}")
    public String postUpdateQuestion(@PathVariable Long id,
                               @RequestParam String content,
                               @RequestParam float points,
                               @RequestParam(required = false) List<String> choicesContent,
                               @RequestParam(required = false) List<Integer> isCorrect,
                               @RequestParam(required = false) String correctAnswer) {
        Question q = this.questionService.getQuestionById(id);
        q.setContent(content);
        q.setPoints(points);

        if (choicesContent != null && q.getChoices() != null) {
            for (int i = 0; i < q.getChoices().size(); i++) {
                Choice choice = q.getChoices().get(i);
                choice.setContent(choicesContent.get(i));
                choice.setIsCorrect(isCorrect != null && isCorrect.contains(i));
            }
        }

        if (correctAnswer != null) {
            q.setCorrectAnswer(correctAnswer);
        }

        Long examId = q.getExam().getId();
        this.questionService.handleSaveQuestion(q);
        return "redirect:/admin/exam/" + examId;
    }

    @GetMapping("/admin/exam/question/delete/{id}")
    public String getDeleteQuestion(@PathVariable Long id) {
        Question question = this.questionService.getQuestionById(id);
        long examId = question.getExam().getId();
        System.out.println("exam>>>>"+examId);
        this.questionService.deleteAQuestion(id);
        return "redirect:/admin/exam/" + examId;
    }




}

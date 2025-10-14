package com.example.exam_portal.controller.admin;


import java.time.LocalDateTime;
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
import com.example.exam_portal.domain.ExamBank;
import com.example.exam_portal.domain.Grade;
import com.example.exam_portal.domain.Question;
import com.example.exam_portal.domain.Subject;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.domain.enums.ExamStatus;
import com.example.exam_portal.domain.enums.ExamType;
import com.example.exam_portal.domain.enums.QuestionType;
import com.example.exam_portal.service.ExamBankService;
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
    private final ExamBankService examBankService;

    public ExamController(ExamService examService, UserService userService, 
    QuestionService questionService, SubjectService subjectService, GradeService gradeService,
    ExamBankService examBankService){
        this.examService=examService;
        this.userService=userService;
        this.questionService=questionService;
        this.subjectService=subjectService;
        this.gradeService=gradeService;
        this.examBankService=examBankService;
    }

    @GetMapping("/admin/exam")
    public String getExamTypePage(Model model) {
        // Lấy tất cả ExamType để hiển thị
        model.addAttribute("examTypes", ExamType.values());
        return "admin/exam/type_list";
    }

    @GetMapping("/admin/examapprove")
    public String getExamAmapproveTypePage(Model model) {
        // Lấy tất cả ExamType để hiển thị
        model.addAttribute("examTypes", ExamType.values());
        return "admin/examapprove/type_list";
    }

    @GetMapping("/admin/examapprove/type/{type}")
    public String getExamAmapprovePageByType(
            @PathVariable("type") ExamType type,
            Model model,
            @RequestParam("page") Optional<String> pageOptional,
            @AuthenticationPrincipal UserDetails userDetails) {

        User teacher = this.userService.getUserByEmail(userDetails.getUsername());
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {
            page = 1;
        }

        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<Exam> examPage;

        
        examPage = this.examService.getAllExamByTypeAndSubject(type, teacher.getTeacher().getSubject() , pageable);
        

        model.addAttribute("exams", examPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", examPage.getTotalPages());
        model.addAttribute("selectedType", type);
        return "admin/examapprove/show";
    }


    @GetMapping("/admin/exam/type/{type}")
    public String getExamPageByType(
            @PathVariable("type") ExamType type,
            Model model,
            @RequestParam("page") Optional<String> pageOptional,
            @AuthenticationPrincipal UserDetails userDetails) {

        User teacher = this.userService.getUserByEmail(userDetails.getUsername());
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {
            page = 1;
        }

        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<Exam> examPage;

        // boolean isAdmin = teacher.getRoles().stream()
        //         .anyMatch(role -> role.getName().equalsIgnoreCase("PRINCIPAL"));
        
        
        examPage = this.examService.getAllExamByTypeAndTeacherId(type, teacher.getId(), pageable);
        

        model.addAttribute("exams", examPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", examPage.getTotalPages());
        model.addAttribute("selectedType", type);
        return "admin/exam/show";
    }

    @GetMapping("/admin/examapprove/approve/{examId}")
    public String approveExam(@PathVariable("examId") Long examId) {
        Exam exam = examService.getExamById(examId);
        if (exam != null) {

            // Cập nhật trạng thái đề thi
            exam.setStatus(ExamStatus.APPROVED);
            examService.handleSaveExam(exam);

            // Thêm bản ghi vào ExamBank nếu chưa có
            ExamBank bank = new ExamBank();
            bank.setExam(exam);
            bank.setCreatedAt(LocalDateTime.now());
            bank.setUpdatedAt(LocalDateTime.now());
            examBankService.handleSaveExam(bank);
        }
        exam.getExamType();

        return "redirect:/admin/examapprove/type/" + exam.getExamType(); // quay lại trang danh sách đề
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

        // gán loại đề thi
        exam.setExamType(form.getExamType());

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
                                
        // cập nhật loại đề thi
        exam.setExamType(form.getExamType());
                                
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

    @GetMapping("/admin/examapprove/{id}")
    public String getQuestionsexamapprove(@PathVariable Long id, Model model) {
        Exam exam = this.examService.getExamById(id);
        List<Question> questions = this.questionService.getQuestionsByExamId(id);
        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        return "admin/question/view";
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

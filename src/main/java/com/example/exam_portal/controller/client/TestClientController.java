package com.example.exam_portal.controller.client;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.exam_portal.domain.Choice;
import com.example.exam_portal.domain.ClassRoom;
import com.example.exam_portal.domain.Exam;
import com.example.exam_portal.domain.ExamResult;
import com.example.exam_portal.domain.ExamSession;
import com.example.exam_portal.domain.Question;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.service.ClassService;
import com.example.exam_portal.service.ExamResultService;
import com.example.exam_portal.service.StudentService;
import com.example.exam_portal.service.TestService;
import com.example.exam_portal.service.UserService;
import com.google.gson.Gson;


@Controller
public class TestClientController {
    private final TestService testService;
    private final UserService userService;
    private final ExamResultService examResultService;
    private final StudentService studentService;
    private final ClassService classService;

    public TestClientController(TestService testService, UserService userService, ExamResultService examResultService,
    StudentService studentService, ClassService classService){
        this.testService=testService;
        this.userService=userService;
        this.examResultService=examResultService;
        this.studentService=studentService;
        this.classService=classService;
    }

    @GetMapping("/exam")
    public String getExamClient(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Lấy User
        User user = this.userService.getUserByEmail(userDetails.getUsername());
    
        // Lấy danh sách lớp mà học sinh đang học
        List<ClassRoom> classRooms = this.classService.getClassRoomByStudentId(user.getId());
    
        if (classRooms.isEmpty()) {
            model.addAttribute("message", "Bạn chưa được thêm vào lớp nào.");
            return "client/exam/examclient";
        }
    
        // Giả sử học sinh chỉ thuộc 1 lớp
        Long classId = classRooms.get(0).getId();
    
        // Lấy các kỳ thi thuộc lớp đó
        List<ExamSession> allExamSessions = this.testService.getAllExamSessionListClassId(List.of(classId));
    
        // Lọc ra ca thi chưa bị khóa
        List<ExamSession> examSessions = allExamSessions.stream()
                .filter(es -> Boolean.FALSE.equals(es.getIsLocked()))
                .toList();
    
        model.addAttribute("examSessions", examSessions);
        return "client/exam/examclient";
    }




    
    
    @GetMapping("/exam/confirm/{id}")
    public String getConfirmExamClient(Model model, @PathVariable long id,  @AuthenticationPrincipal UserDetails userDetails) {
        User student = this.userService.getUserByEmail(userDetails.getUsername());


        // Lấy các kỳ thi thuộc những lớp đó
        ExamSession examSessions = this.testService.getExamSessionById(id);

        model.addAttribute("examSession", examSessions);
         // Thymeleaf template
        return "client/exam/confirmexam";
    }

    @GetMapping("/exam/confirm/start/{id}")
    public String getStartExamClient(Model model,
                                     @PathVariable long id,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        // Lấy user
        User user = this.userService.getUserByEmail(userDetails.getUsername());
                                    
        // Lấy danh sách lớp học của học sinh (qua bảng class_students)
        List<ClassRoom> studentClasses = this.classService.getClassRoomByStudentId(user.getId());
                                    
        if (studentClasses.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Bạn chưa được thêm vào lớp nào.");
            return "redirect:/exam";
        }
    
        ExamSession examSession = this.testService.getExamSessionById(id);
    
        // Kiểm tra học sinh có thuộc lớp thi này không
        boolean isStudentInClass = studentClasses.stream()
                .anyMatch(c -> c.getId().equals(examSession.getClassroom().getId()));
    
        if (!isStudentInClass) {
            redirectAttributes.addFlashAttribute("error", "Bạn không thuộc lớp của kỳ thi này.");
            return "redirect:/exam";
        }
    
        Exam exam = examSession.getExam();
    
        // Kiểm tra đã làm bài chưa
        boolean hasSubmitted = examResultService.hasStudentSubmittedExam(user.getId(), exam.getId());
        if (hasSubmitted) {
            redirectAttributes.addFlashAttribute("error", "Bạn đã hoàn thành kỳ thi này.");
            return "redirect:/exam";
        }
    
        // Kiểm tra thời gian
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(examSession.getStartTime()) || now.isAfter(examSession.getEndTime())) {
            redirectAttributes.addFlashAttribute("error", "Chưa đến hoặc đã quá thời gian làm bài.");
            return "redirect:/exam";
        }
    
        model.addAttribute("examSession", examSession);
        model.addAttribute("questions", exam.getQuestions());
    
        return "client/exam/startexam";
    }



    @PostMapping("/exam/submit")
    public String submitExam(@RequestParam Map<String, String> params,
                             @RequestParam MultiValueMap<String, String> multiParams,
                             @RequestParam("examSessionId") Long examSessionId,
                             @RequestParam(value = "autoSubmit", defaultValue = "false") boolean autoSubmit,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
                            
        User student = userService.getUserByEmail(userDetails.getUsername());
        ExamSession session = testService.getExamSessionById(examSessionId);
        Exam exam = session.getExam();
                            
        float totalPoints = 0f;
        float score = 0f;
                            
        Map<Long, Object> userAnswers = new HashMap<>();
        boolean hasAnyAnswer = false; // <-- để xác định người dùng có làm bài không
                            
        for (Question q : exam.getQuestions()) {
            String paramKey = "answers[" + q.getId() + "]";
            totalPoints += q.getPoints();
        
            switch (q.getType()) {
                case SINGLE:
                case TRUE_FALSE:
                    String singleAns = params.get(paramKey);
                    if (singleAns != null && !singleAns.trim().isEmpty()) {
                        hasAnyAnswer = true;
                        userAnswers.put(q.getId(), singleAns);
                    
                        Choice correct = q.getChoices().stream()
                            .filter(Choice::getIsCorrect)
                            .findFirst().orElse(null);
                    
                        if (correct != null && correct.getContent().equalsIgnoreCase(singleAns.trim())) {
                            score += q.getPoints();
                        }
                    }
                    break;
                
                case MULTIPLE:
                    List<String> multiAns = multiParams.get(paramKey);
                    if (multiAns != null && !multiAns.isEmpty()) {
                        hasAnyAnswer = true;
                        userAnswers.put(q.getId(), multiAns);
                    
                        List<String> correctAnswers = q.getChoices().stream()
                            .filter(Choice::getIsCorrect)
                            .map(Choice::getContent)
                            .sorted()
                            .toList();
                    
                        List<String> userSelected = multiAns.stream().map(String::trim).sorted().toList();
                    
                        if (userSelected.equals(correctAnswers)) {
                            score += q.getPoints();
                        }
                    }
                    break;
                
                case FILL_IN_BLANK:
                    String inputAns = params.get(paramKey);
                    if (inputAns != null && !inputAns.trim().isEmpty()) {
                        hasAnyAnswer = true;
                        userAnswers.put(q.getId(), inputAns);
                    
                        if (q.getCorrectAnswer() != null &&
                            q.getCorrectAnswer().trim().equalsIgnoreCase(inputAns.trim())) {
                            score += q.getPoints();
                        }
                    }
                    break;
            }
        }
    
        // Nếu không trả lời câu nào => cho 0 điểm và đánh dấu gian lận
        float finalScore = (totalPoints == 0) ? 0f : (score / totalPoints) * 10;
    
        ExamResult result = new ExamResult();
        result.setStudent(student);
        result.setExam(exam);
        result.setExamSession(session);
        result.setScore(hasAnyAnswer ? score : 0f);
        result.setSubmittedAt(LocalDateTime.now());
        result.setAnswersJson(new Gson().toJson(userAnswers));
        result.setDurationUsed(0); // TODO: Tính thời gian nếu muốn
    
        if (autoSubmit && userAnswers.isEmpty()) {
            result.setScore(0f);
        }
    
    
        examResultService.handleSaveExam(result);
    
        model.addAttribute("result", result);
        model.addAttribute("exam", exam);
        return "client/exam/result";
    }

    
}

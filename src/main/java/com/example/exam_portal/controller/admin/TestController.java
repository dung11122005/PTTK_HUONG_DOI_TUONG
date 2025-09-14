package com.example.exam_portal.controller.admin;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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

import com.example.exam_portal.domain.ClassRoom;
import com.example.exam_portal.domain.Exam;
import com.example.exam_portal.domain.ExamResult;
import com.example.exam_portal.domain.ExamSession;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.service.ClassService;
import com.example.exam_portal.service.ExamResultService;
import com.example.exam_portal.service.ExamService;
import com.example.exam_portal.service.TestService;
import com.example.exam_portal.service.UserService;

import jakarta.servlet.http.HttpServletResponse;


@Controller
public class TestController {
    private final TestService testService;
    private final UserService userService;
    private final ExamService examService;
    private final ClassService classService;
    private final ExamResultService examResultService;


    public TestController(TestService testService, UserService userService, 
        ExamService examService, ClassService classService,
        ExamResultService examResultService){
        this.testService=testService;
        this.userService=userService;
        this.examService=examService;
        this.classService=classService;
        this.examResultService=examResultService;
    }


    @GetMapping("/admin/test")
    public String getTestPage(Model model, @RequestParam("page") Optional<String> pageOptional,
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
        Page<ExamSession> ex;
        Pageable pageable = PageRequest.of(page - 1, 10);
        
        boolean isAdmin = teacher.getRoles().stream()
        .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));

        if(isAdmin){
            ex = this.testService.getAllExamSessionPagination(pageable);
        }else{
            ex = this.testService.getAllExamSessionPaginationTeacherId(teacher.getId(), pageable);
        }

        List<ExamSession> examSessions =  ex.getContent();
        model.addAttribute("examSessions", examSessions);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ex.getTotalPages());
        return "admin/test/show";
    }

    @GetMapping("/admin/test/{id}")
    public String getResultTestPage(Model model, @RequestParam("page") Optional<String> pageOptional,
    @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
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
        Page<ExamResult> ex;
        Pageable pageable = PageRequest.of(page - 1, 10);
        
        ex = this.examResultService.getAllResulrExamPaginationTeacherId(id, pageable);
        

        List<ExamResult> examResults =  ex.getContent();
        model.addAttribute("examResults", examResults);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ex.getTotalPages());
        return "admin/test/detail";
    }

    @GetMapping("/admin/test/{id}/export")
    public void exportToExcelExamResult(@PathVariable Long id, HttpServletResponse response,
                              @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        List<ExamResult> examResults;
        
        examResults = this.examResultService.getAllExamResultSessionId(id);

        // Thiết lập header response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);

        // Tên file có thêm thời gian
        String fileName = "exam-results_" + timestamp + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        // Ghi file Excel
        this.examResultService.writeExcelFileExamResult(examResults, response.getOutputStream());
    }

    @GetMapping("/admin/test/create")
    public String getCreateTestPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User teacher = this.userService.getUserByEmail(userDetails.getUsername());
        List<Exam> exams;

        boolean isAdmin = teacher.getRoles().stream()
        .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));

        if(isAdmin){
            exams=this.examService.getAllExam();
        }else{
            exams=this.examService.getExamByTeacherId(teacher.getId());
        }
        List<ClassRoom> classRoom;
        if(isAdmin){
            classRoom=this.classService.getAllClassRoom();
        }else{
            classRoom=this.classService.getClassByTeacherId(teacher.getId());
        }

        
        model.addAttribute("newExamSession", new ExamSession());
        model.addAttribute("exams", exams);
        model.addAttribute("classRooms", classRoom);
        return "admin/test/create";
    }

    @PostMapping("/admin/test/create")
    public String postCreateTestPage(@ModelAttribute("newExamSession") ExamSession newExamSession,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        User teacher = this.userService.getUserByEmail(userDetails.getUsername());

        // Lấy lại exam và classRoom từ ID
        Exam exam = this.examService.getExamById(newExamSession.getExam().getId());
        ClassRoom classroom = this.classService.getClassRoomById(newExamSession.getClassroom().getId());

        // Gán lại vào examSession
        newExamSession.setExam(exam);
        newExamSession.setClassroom(classroom);
        newExamSession.setTeacher(teacher);

        this.testService.handleSaveExamSession(newExamSession);

        return "redirect:/admin/test"; // nhớ redirect tránh reload form
    }

    @GetMapping("/admin/test/update/{id}")
    public String getUpdateTestPage(Model model, @AuthenticationPrincipal UserDetails userDetails,
                @PathVariable Long id) {
        User teacher = this.userService.getUserByEmail(userDetails.getUsername());
        List<Exam> exams;

        boolean isAdmin = teacher.getRoles().stream()
        .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));
        if(isAdmin){
            exams=this.examService.getAllExam();
        }else{
            exams=this.examService.getExamByTeacherId(teacher.getId());
        }
        List<ClassRoom> classRoom;
        if(isAdmin){
            classRoom=this.classService.getAllClassRoom();
        }else{
            classRoom=this.classService.getClassByTeacherId(teacher.getId());
        }
        ExamSession newExamSession=this.testService.getExamSessionById(id);
        
        model.addAttribute("newExamSession", newExamSession);
        model.addAttribute("exams", exams);
        model.addAttribute("classRooms", classRoom);
        return "admin/test/update";
    }

    @PostMapping("/admin/test/update/{id}")
    public String postUpdateTestPage(@ModelAttribute("newExamSession") ExamSession newExamSession,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     @PathVariable Long id) {
        User teacher = this.userService.getUserByEmail(userDetails.getUsername());

        newExamSession.setId(id);
        // Lấy lại exam và classRoom từ ID
        Exam exam = this.examService.getExamById(newExamSession.getExam().getId());
        ClassRoom classroom = this.classService.getClassRoomById(newExamSession.getClassroom().getId());

        // Gán lại vào examSession
        newExamSession.setExam(exam);
        newExamSession.setClassroom(classroom);
        newExamSession.setTeacher(teacher);

        this.testService.handleSaveExamSession(newExamSession);

        return "redirect:/admin/test"; // nhớ redirect tránh reload form
    }

    @GetMapping("/admin/test/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        return "admin/test/delete";
    }

    @PostMapping("/admin/test/delete")
    public String postDeleteUser(@RequestParam("id") Long id) {
        this.testService.deleteAExamSession(id);
        return "redirect:/admin/exam";
    }

    @GetMapping("/admin/test/result/{id}")
    public String getDeleteExamResult(@RequestParam("testId") Long testId, @PathVariable long id) {
        this.examResultService.hanndleDeleteExamResult(id);
        return "redirect:/admin/test/"+testId;
    }
    

}

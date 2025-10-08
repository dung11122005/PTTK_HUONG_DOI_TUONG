package com.example.exam_portal.controller.admin;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exam_portal.domain.AcademicYear;
import com.example.exam_portal.domain.ClassRoom;
import com.example.exam_portal.domain.ClassStudent;
import com.example.exam_portal.domain.Grade;
import com.example.exam_portal.domain.Subject;
import com.example.exam_portal.domain.TeachingAssignment;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.service.AcademicYearService;
import com.example.exam_portal.service.ClassService;
import com.example.exam_portal.service.GradeService;
import com.example.exam_portal.service.SubjectService;
import com.example.exam_portal.service.TeachingAssignmentService;
import com.example.exam_portal.service.UserService;
import com.example.exam_portal.spec.SpecificationBuilder;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ClassController {
    
    private final ClassService classService;
    private final UserService userService;
    private final AcademicYearService academicYearService;
    private final GradeService gradeService;
    private final SubjectService subjectService;
    private final TeachingAssignmentService teachingAssignmentService;

    public ClassController(ClassService classService, UserService userService, 
    AcademicYearService academicYearService, GradeService gradeService, SubjectService subjectService,
    TeachingAssignmentService teachingAssignmentService){
        this.classService=classService;
        this.userService=userService;
        this.academicYearService=academicYearService;
        this.gradeService=gradeService;
        this.subjectService=subjectService;
        this.teachingAssignmentService=teachingAssignmentService;
    }

    @GetMapping("/admin/class")
    public String getAcademicYearPage(Model model) {
        List<AcademicYear> years = academicYearService.getAllAcademicYear();
        model.addAttribute("years", years);
        return "admin/class/years";
    }


    @GetMapping("/admin/class/{yearId}")
    public String getClassByAcademicYear(
            @PathVariable("yearId") long yearId,
            Model model,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.getUserByEmail(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page - 1, 12);
        Page<ClassRoom> classPage;

        boolean isAcademicAffairs = user.getRoles().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase("ACADEMIC_AFFAIRS_OFFICE"));
        boolean isHomeroomTeacher = user.getRoles().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase("HOMEROOM_TEACHER"));

        if (isAcademicAffairs) {
            classPage = classService.getClassByAcademicYear(yearId, pageable);
        } else if (isHomeroomTeacher) {
            classPage = classService.getClassByTeacherAndAcademicYear(user.getId(), yearId, pageable);
        } else {
            classPage = Page.empty(pageable);
        }

        model.addAttribute("classRooms", classPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", classPage.getTotalPages());
        model.addAttribute("yearId", yearId);

        return "admin/class/show"; // file Thymeleaf hiển thị lớp
    }




    @GetMapping("/admin/class/create")
    public String getCreateClassPage(Model model) {
        model.addAttribute("newClass", new ClassRoom());

        List<AcademicYear> academicYears = academicYearService.getAllAcademicYear();
        List<Grade> grades = gradeService.getAllGrade();
        List<Subject> subjects = this.subjectService.getAllSubject();

        model.addAttribute("academicYears", academicYears);
        model.addAttribute("grades", grades);
        model.addAttribute("subjects", subjects);

        return "admin/class/create";
    }




    @PostMapping("/admin/class/create")
    public String postCreateClass(
            @ModelAttribute("newClass") ClassRoom classroom,
            @RequestParam("teacherEmail") String homeroomEmail,
            @RequestParam(value = "subjectTeacherEmails", required = false) List<String> subjectTeacherEmails) {
            
        // Tìm và gán giáo viên chủ nhiệm
        User homeroom = userService.getUserByEmail(homeroomEmail);
        if (homeroom == null) {
            throw new RuntimeException("Không tìm thấy giáo viên chủ nhiệm với email: " + homeroomEmail);
        }
        classroom.setHomeroomTeacher(homeroom);
    
        // Lưu lớp trước (để có classroom_id)
        classService.handleSaveClassRoom(classroom);
    
        // Lưu danh sách giáo viên bộ môn (chỉ email, không có môn)
        if (subjectTeacherEmails != null && !subjectTeacherEmails.isEmpty()) {
            for (String email : subjectTeacherEmails) {
                if (email != null && !email.trim().isEmpty()) {
                    User teacher = userService.getUserByEmail(email.trim());
                    if (teacher == null) {
                        throw new RuntimeException("Không tìm thấy giáo viên với email: " + email);
                    }
                
                    TeachingAssignment assignment = new TeachingAssignment();
                    assignment.setTeacher(teacher);
                    assignment.setClassroom(classroom);
                
                    this.teachingAssignmentService.handleSaveTeachingAssignment(assignment);
                }
            }
        }
    
        return "redirect:/admin/class";
    }



    @GetMapping("/admin/class/update/{id}")
    public String getUpdateClassPage(Model model, @PathVariable Long id) {
        ClassRoom classRoom = classService.getClassRoomById(id);
        model.addAttribute("newClass", classRoom);

        model.addAttribute("academicYears", academicYearService.getAllAcademicYear());
        model.addAttribute("grades", gradeService.getAllGrade());

        // danh sách subject để hiển thị
        model.addAttribute("subjects", subjectService.getAllSubject());

        // danh sách TeachingAssignment hiện tại
        model.addAttribute("teachingAssignments", teachingAssignmentService.getByClassRoomId(id));

        return "admin/class/update";
    }



    @PostMapping("/admin/class/update/{id}")
    public String postUpdateClass(
            @ModelAttribute("newClass") ClassRoom classroom,
            @PathVariable Long id,
            @RequestParam("homeroomTeacherEmail") String homeroomEmail,
            @RequestParam Map<String, String> requestParams) {

        // Gán GVCN
        User homeroom = userService.getUserByEmail(homeroomEmail);
        if (homeroom == null) {
            throw new RuntimeException("Không tìm thấy giáo viên chủ nhiệm với email: " + homeroomEmail);
        }
        classroom.setId(id);
        classroom.setHomeroomTeacher(homeroom);

        classService.handleSaveClassRoom(classroom);

        // Xóa phân công cũ
        teachingAssignmentService.deleteByClassRoomId(id);

        // Thêm phân công mới từ form
        int i = 0;
        while (true) {
            String email = requestParams.get("subjectTeachers[" + i + "].teacherEmail");
            String subjectIdStr = requestParams.get("subjectTeachers[" + i + "].subjectId");
            if (email == null || subjectIdStr == null) break;

            User teacher = userService.getUserByEmail(email);
            Optional<Subject> subject = subjectService.getSubjectById(Long.parseLong(subjectIdStr));

            TeachingAssignment assignment = new TeachingAssignment();
            assignment.setTeacher(teacher);
            assignment.setClassroom(classroom);

            teachingAssignmentService.handleSaveTeachingAssignment(assignment);
            i++;
        }

        return "redirect:/admin/class";
    }



    @GetMapping("/admin/class/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        return "admin/class/delete";
    }

    @PostMapping("/admin/class/delete")
    public String postDeleteUser(@RequestParam("id") Long id) {
        this.classService.deleteAClassRoom(id);
        return "redirect:/admin/class";
    }


    @GetMapping("/admin/class/student/add/{classId}")
    public String showAddStudentToClassForm(Model model, @PathVariable Long classId, 
    @RequestParam Map<String, String> params, 
    @RequestParam("page") Optional<String> pageOptional ) {
        ClassRoom classRoom = this.classService.getClassRoomById(classId);
        List<User> listStudents = this.userService.getUserRoleName("STUDENT"); // chỉ học sinh

        // Lọc bỏ các param phân trang, sort
        Map<String, String> filteredParams = params.entrySet().stream()
        .filter(e -> !List.of("page", "size", "sort").contains(e.getKey()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Specification<ClassStudent> spec = new SpecificationBuilder<ClassStudent>()
        .buildFromParams(filteredParams)
        .and((root, query, cb) -> cb.equal(root.get("classroom").get("id"), classId));


        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            }
        } catch (Exception e) {

        }
        Page<ClassStudent> classStudentList;
        Pageable pageable = PageRequest.of(page - 1, 10);

        classStudentList=this.classService.getAllClassRoomPagination(spec, pageable);

        model.addAttribute("classStudentList", classStudentList);
        model.addAttribute("currentClass", classRoom); // để lấy ID truyền vào form action
        model.addAttribute("students", listStudents);  // danh sách học sinh
        model.addAttribute("classStudent", new ClassStudent()); // object gắn với form

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", classStudentList.getTotalPages());

        return "admin/class/addStudent"; // tên template Thymeleaf
    }

    @GetMapping("/admin/class/student/add/{classId}/export")
    public void exportToExcelClassStudent(@PathVariable("classId") Long id, HttpServletResponse response,
                              @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        List<ClassStudent> classStudents;
        
        classStudents = this.classService.getClassStudentById(id);

        // Thiết lập header response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);

        // Tên file có thêm thời gian
        String fileName = "Class-student" + timestamp + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        // Ghi file Excel
        this.classService.writeExcelFileClassStudent(classStudents, response.getOutputStream());
    }

    @PostMapping("/admin/class/student/add/{classId}")
    public String addStudentToClass(@PathVariable Long classId,
                                    @RequestParam("email") String email,
                                    Model model) {

        ClassRoom classRoom = this.classService.getClassRoomById(classId);
        model.addAttribute("currentClass", classRoom);

        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng nhập email");
            return "admin/class/addStudent"; // Tên file HTML của form
        }

        User student = this.userService.getUserByEmail(email);
        if (student == null) {
            model.addAttribute("error", "Không tìm thấy học sinh với email này");
            return "admin/class/addStudent";
        }

        ClassStudent classStudent = new ClassStudent();
        classStudent.setClassroom(classRoom);
        classStudent.setStudent(student);

        classRoom.getStudents().add(classStudent);
        this.classService.handleSaveClassRoom(classRoom);

        return "redirect:/admin/class/student/add/" + classId;
    }



    @PostMapping("/admin/class/student/delete/{classId}/{studentId}")
    public String deleteStudentFromClass(@PathVariable Long classId,
                                         @PathVariable Long studentId) {
        this.classService.deleteAClassAndStudent(classId, studentId);
        return "redirect:/admin/class/student/add/" + classId;
    }
    
    
}

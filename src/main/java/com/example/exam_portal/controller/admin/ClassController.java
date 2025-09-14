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

import com.example.exam_portal.domain.ClassRoom;
import com.example.exam_portal.domain.ClassStudent;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.service.ClassService;
import com.example.exam_portal.service.UserService;
import com.example.exam_portal.spec.SpecificationBuilder;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ClassController {
    
    private final ClassService classService;
    private final UserService userService;

    public ClassController(ClassService classService, UserService userService){
        this.classService=classService;
        this.userService=userService;
    }

    @GetMapping("/admin/class")
    public String getClassPage(Model model, @RequestParam("page") Optional<String> pageOptional, 
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

        Page<ClassRoom> cl;
        Pageable pageable = PageRequest.of(page - 1, 10);
        boolean isAdmin = teacher.getRoles().stream()
        .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));

        if (isAdmin) {
            cl = this.classService.getAllClassRoomPagination(pageable);
        } else {
            cl = this.classService.getAllClassRoomPaginationByIdTeacher(teacher.getId(), pageable);
        }

        
        List<ClassRoom> classRooms = cl.getContent();
        model.addAttribute("classRooms", classRooms);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", cl.getTotalPages());
        return "admin/class/show";
    }


    @GetMapping("/admin/class/create")
    public String getCreateClassPage(Model model) {
        model.addAttribute("newClass", new ClassRoom()); // Dùng chính đối tượng đã gán teacher
        return "admin/class/create";
    }


    // @PostMapping("/admin/class/create")
    // public String postCreateClass(@ModelAttribute("newClass") ClassRoom classroom, @AuthenticationPrincipal UserDetails userDetails) {
    //     User teacher = this.userService.getUserByEmail(userDetails.getUsername());
    //     classroom.setTeacher(teacher);
    //     this.classService.handleSaveClassRoom(classroom);
    //     return "redirect:/admin/class"; // hoặc trang hiển thị danh sách lớp
    // }

    @GetMapping("/admin/class/update/{id}")
    public String getUpdateClassPage(Model model, @PathVariable Long id) {
        ClassRoom classRoom=this.classService.getClassRoomById(id);
        model.addAttribute("newClass", classRoom); // Dùng chính đối tượng đã gán teacher
        return "admin/class/update";
    }


    // @PostMapping("/admin/class/update/{id}")
    // public String postUpdateClass(@ModelAttribute("newClass") ClassRoom classroom,
    //                               @PathVariable Long id,
    //                               @AuthenticationPrincipal UserDetails userDetails) {
    //     User teacher = this.userService.getUserByEmail(userDetails.getUsername());

    //     classroom.setId(id);
    //     classroom.setTeacher(teacher);

    //     this.classService.handleSaveClassRoom(classroom);
    //     return "redirect:/admin/class";
    // }

    @GetMapping("/admin/class/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        return "admin/class/delete";
    }

    @PostMapping("/admin/class/delete")
    public String postDeleteUser(@RequestParam("id") Long id) {
        this.classService.deleteAClassRoom(id);
        return "redirect:/admin/exam";
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

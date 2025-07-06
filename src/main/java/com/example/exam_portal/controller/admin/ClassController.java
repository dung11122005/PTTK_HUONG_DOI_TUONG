package com.example.exam_portal.controller.admin;

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
import com.example.exam_portal.domain.ClassStudent;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.service.ClassService;
import com.example.exam_portal.service.UserService;

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
        if(teacher.getRole().getName().equals("ADMIN")){
            cl = this.classService.getAllClassRoomPagination(pageable);
        }else{
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


    @PostMapping("/admin/class/create")
    public String postCreateClass(@ModelAttribute("newClass") ClassRoom classroom, @AuthenticationPrincipal UserDetails userDetails) {
        User teacher = this.userService.getUserByEmail(userDetails.getUsername());
        classroom.setTeacher(teacher);
        this.classService.handleSaveClassRoom(classroom);
        return "redirect:/admin/class"; // hoặc trang hiển thị danh sách lớp
    }

    @GetMapping("/admin/class/update/{id}")
    public String getUpdateClassPage(Model model, @PathVariable Long id) {
        ClassRoom classRoom=this.classService.getClassRoomById(id);
        model.addAttribute("newClass", classRoom); // Dùng chính đối tượng đã gán teacher
        return "admin/class/update";
    }


    @PostMapping("/admin/class/update/{id}")
    public String postUpdateClass(@ModelAttribute("newClass") ClassRoom classroom,
                                  @PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        User teacher = this.userService.getUserByEmail(userDetails.getUsername());

        classroom.setId(id);
        classroom.setTeacher(teacher);

        this.classService.handleSaveClassRoom(classroom);
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
        return "redirect:/admin/exam";
    }


    @GetMapping("/admin/class/student/add/{classId}")
    public String showAddStudentToClassForm(Model model, @PathVariable Long classId) {
        ClassRoom classRoom = this.classService.getClassRoomById(classId);
        List<User> listStudents = this.userService.getUserRoleName("STUDENT"); // chỉ học sinh

        List<ClassStudent> classStudentList=this.classService.getClassStudentById(classId);

        model.addAttribute("classStudentList", classStudentList);
        model.addAttribute("currentClass", classRoom); // để lấy ID truyền vào form action
        model.addAttribute("students", listStudents);  // danh sách học sinh
        model.addAttribute("classStudent", new ClassStudent()); // object gắn với form

        return "admin/class/addStudent"; // tên template Thymeleaf
    }


    @PostMapping("/admin/class/student/add/{classId}")
    public String addStudentToClass(@PathVariable Long classId,
                                    @ModelAttribute("classStudent") ClassStudent classStudent) {

        ClassRoom classRoom = this.classService.getClassRoomById(classId);
        User student = this.userService.getUserById(classStudent.getStudent().getId());

        classStudent.setClassroom(classRoom);
        classStudent.setStudent(student);

        // Thêm vào danh sách lớp
        classRoom.getStudents().add(classStudent);

        // Lưu lớp => cascade sẽ lưu classStudent
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

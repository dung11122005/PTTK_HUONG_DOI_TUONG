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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.exam_portal.domain.Chapter;
import com.example.exam_portal.domain.Course;
import com.example.exam_portal.domain.CourseLesson;
import com.example.exam_portal.domain.Purchase;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.service.CourseService;
import com.example.exam_portal.service.PurchaseService;
import com.example.exam_portal.service.UploadService;
import com.example.exam_portal.service.UserService;


@Controller
public class CourseAdminController {
    private final CourseService courseService;
    private final UserService userService;
    private final UploadService uploadService;
    private final PurchaseService purchaseService;

    public CourseAdminController(CourseService courseService, 
    UserService userService, UploadService uploadService, PurchaseService purchaseService){
        this.courseService=courseService;
        this.userService=userService;
        this.uploadService=uploadService;
        this.purchaseService=purchaseService;
    }

    @GetMapping("/admin/course")
    public String getCoursePage(Model model, @RequestParam("page") Optional<String> pageOptional,
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
        Page<Course> us;
        Pageable pageable = PageRequest.of(page - 1, 10);
        boolean isAdmin = teacher.getRoles().stream()
        .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));
        if(isAdmin){
            us = this.courseService.getAllCoursePagination(pageable);
        }else{
            us = this.courseService.getAllCoursePaginationTeacherId(teacher.getId(), pageable);
        }
        
        List<Course> courses = us.getContent();
        model.addAttribute("courses", courses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", us.getTotalPages());
        return "admin/course/show";
    }


    @GetMapping("/admin/course/create")
    public String getCreateCoursePage(Model model) {
        model.addAttribute("newCourse", new Course());
        return "admin/course/create";
    }

    @PostMapping("/admin/course/create")
    public String postCreateCourse(@ModelAttribute("newCourse") Course course,
                             @AuthenticationPrincipal UserDetails userDetails,
                             BindingResult newCourseBindingResult,
            @RequestParam("thumbnailFile") MultipartFile file) {
        User teacher = this.userService.getUserByEmail(userDetails.getUsername());

        if (newCourseBindingResult.hasErrors()) {
            return "admin/course/create";
        }

        String avatar = this.uploadService.handleSaveUploadFile(file, "avatarcourses");
        

        course.setThumbnail(avatar);
        // GÁN GIÁO VIÊN TẠO KHÓA HỌC
        course.setTeacher(teacher);

        // Save
        this.courseService.handleSaveCourse(course);

        return "redirect:/admin/course";
    }

    @GetMapping("/admin/course/update/{id}")
    public String getUpdateUserPage(Model model, @PathVariable long id) {
        Course course = this.courseService.getCourseById(id);
        model.addAttribute("newCourse", course);
        return "admin/course/update";
    }

    @PostMapping("/admin/course/update/{id}")
    public String postUpdateCourse(@ModelAttribute("newCourse") Course course,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   BindingResult newCourseBindingResult,
                                   @RequestParam("thumbnailFile") MultipartFile file,
                                   @PathVariable long id) {
        User teacher = this.userService.getUserByEmail(userDetails.getUsername());

        if (newCourseBindingResult.hasErrors()) {
            return "admin/course/update";
        }

        // Lấy course cũ từ DB
        Course existingCourse = this.courseService.getCourseById(id);
        if (existingCourse == null) {
            return "redirect:/admin/course"; // hoặc trả về lỗi phù hợp
        }

        course.setId(id);
        course.setTeacher(teacher);

        // Nếu có ảnh mới thì lưu ảnh mới, ngược lại giữ ảnh cũ
        if (!file.isEmpty()) {
            String avatar = this.uploadService.handleSaveUploadFile(file, "avatarcourses");
            course.setThumbnail(avatar);
        } else {
            course.setThumbnail(existingCourse.getThumbnail());
        }

        // Save
        this.courseService.handleSaveCourse(course);

        return "redirect:/admin/course";
    }



    @GetMapping("/admin/course/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        return "admin/course/delete";
    }

    @PostMapping("/admin/course/delete")
    public String postDeleteUser(@RequestParam("id") Long id) {
        this.courseService.deleteCourse(id);
        return "redirect:/admin/course";
    }


    @GetMapping("/admin/course/{id}")
    public String getCreateChapter(Model model, @PathVariable long id) {
        Course course=this.courseService.getCourseById(id);
        model.addAttribute("course", course);
        return "admin/course/detail";
    }

    @PostMapping("/admin/course/chapter/create/{courseId}")
    public String postCreateChapter(
            @PathVariable Long courseId,
            @RequestParam("title") String title,
            @RequestParam("sortOrder") int sortOrder) {

        Course course = this.courseService.getCourseById(courseId);
        

        Chapter chapter = new Chapter();
        chapter.setTitle(title);
        chapter.setSortOrder(sortOrder);
        chapter.setCourse(course); // đảm bảo mối quan hệ chapter -> course

        this.courseService.handleSaveChapter(chapter);

        return "redirect:/admin/course/" + courseId;
    }

    @GetMapping("/admin/course/chapter/delete/{id}")
    public String getDeleteChapter(@PathVariable Long id) {
        Chapter chapter = this.courseService.getChapterById(id).get();

        Long courseId = chapter.getCourse().getId();
        this.courseService.deleteChapter(id);

        return "redirect:/admin/course/" + courseId;
    }

    @PostMapping("/admin/course/lesson/create/{chapterId}")
    public String postCreateCourseLesson(@PathVariable("chapterId") Long chapterId,
                               @RequestParam("title") String title,
                               @RequestParam("videoUrl") String videoUrl,
                               @RequestParam("durationMinutes") Integer durationMinutes,
                               @RequestParam("sortOrder") Integer sortOrder) {

        // Tìm chương học theo ID
        Chapter chapter = this.courseService.getChapterById(chapterId).get();
     

        // Tạo đối tượng bài học mới
        CourseLesson lesson = new CourseLesson();
        lesson.setTitle(title);
        lesson.setVideoUrl(videoUrl);
        lesson.setDurationMinutes(durationMinutes);
        lesson.setSortOrder(sortOrder);
        lesson.setChapter(chapter);

        // Lưu vào database
        this.courseService.handleSaveCourseLesson(lesson);

        // Redirect về trang chi tiết khóa học (giả sử route là như sau)
        return "redirect:/admin/course/" + chapter.getCourse().getId();
    }

    @PostMapping("/admin/course/lesson/update/{id}")
    public String postUpdateCourseLesson(@PathVariable Long id,
                               @RequestParam String title,
                               @RequestParam String videoUrl,
                               @RequestParam Integer durationMinutes,
                               @RequestParam Integer sortOrder) {
        CourseLesson lesson = this.courseService.getCourseLessonById(id).get();

        lesson.setTitle(title);
        lesson.setVideoUrl(videoUrl);
        lesson.setDurationMinutes(durationMinutes);
        lesson.setSortOrder(sortOrder);

        this.courseService.handleSaveCourseLesson(lesson);

        Long courseId = lesson.getChapter().getCourse().getId();
        return "redirect:/admin/course/" + courseId;
    }

    @GetMapping("/admin/course/lesson/delete/{id}")
    public String getDeleteCourseLesson(@PathVariable Long id) {
        CourseLesson lesson = this.courseService.getCourseLessonById(id).get();

        Long courseId = lesson.getChapter().getCourse().getId();
        this.courseService.deleteCourseLesson(lesson.getId());

        return "redirect:/admin/course/" + courseId;
    }

    @GetMapping("/admin/sold")
    public String getCourseSold(Model model, @RequestParam("page") Optional<String> pageOptional,
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

        
        Page<Purchase> us;
        Pageable pageable = PageRequest.of(page - 1, 10);
        List<Course> courses=this.courseService.getCourseByTeacherId(teacher.getId());
        boolean isAdmin = teacher.getRoles().stream()
        .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));
        if(isAdmin){
            us = this.purchaseService.getAllCourseSoldPagination(pageable);
        }else{
            us = this.purchaseService.getAllCourseSoldPaginationListCourseId(courses, pageable);
        }
        
        List<Purchase> purchases = us.getContent();
        model.addAttribute("purchases", purchases);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", us.getTotalPages());
        return "admin/coursesold/show";
    }
}

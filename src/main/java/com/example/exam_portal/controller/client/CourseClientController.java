package com.example.exam_portal.controller.client;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exam_portal.domain.Course;
import com.example.exam_portal.domain.CourseLesson;
import com.example.exam_portal.domain.PaymentRequest;
import com.example.exam_portal.domain.Purchase;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.service.CourseService;
import com.example.exam_portal.service.PaymentService;
import com.example.exam_portal.service.PurchaseService;
import com.example.exam_portal.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;



@Controller
public class CourseClientController {
    private final CourseService courseService;
    private final UserService userService;
    private final PurchaseService purchaseService;
    private final PaymentService paymentService;

    public CourseClientController(CourseService courseService, UserService userService, 
    PurchaseService purchaseService, PaymentService paymentService){
        this.courseService=courseService;
        this.userService=userService;
        this.purchaseService=purchaseService;
        this.paymentService=paymentService;
    }

    @GetMapping("/courses")
    public String getCourseHomePage(Model model, @RequestParam("page") Optional<String> pageOptional,
    @AuthenticationPrincipal UserDetails userDetails) {

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
        Pageable pageable = PageRequest.of(page - 1, 9);
        
        us = this.courseService.getAllCoursePagination(pageable);
      
        
        List<Course> courses = us.getContent();
        model.addAttribute("courses", courses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", us.getTotalPages());

        return "client/course/course";
    }
    
    @GetMapping("/course/detail/{id}")
    public String getCourseDetailtPage(Model model, @PathVariable long id, @AuthenticationPrincipal UserDetails userDetails) {
        Course course=this.courseService.getCourseById(id);
        User user = this.userService.getUserByEmail(userDetails.getUsername());
        int totalLessons = course.getChapters().stream()
            .flatMap(c -> c.getLessons().stream())
            .collect(Collectors.toList())
            .size();

        int totalDuration = course.getChapters().stream()
            .flatMap(c -> c.getLessons().stream())
            .mapToInt(CourseLesson::getDurationMinutes)
            .sum();

        totalDuration=totalDuration/60;

        boolean check=this.purchaseService.checkPurchaseStudentIdAndCourseId(user.getId(), id);
        model.addAttribute("check", check);
        model.addAttribute("course", course);
        model.addAttribute("totalLessons", totalLessons);
        model.addAttribute("totalDuration", totalDuration);
        
        return "client/course/coursedetailt";
    }
    
   @PostMapping("/course/purchase/{courseId}")
    public String postPurchaseCourse(@PathVariable Long courseId,
                                     HttpServletRequest request) throws Exception {
        Long userId = (Long) request.getSession(false).getAttribute("id");

        Course course = courseService.getCourseById(courseId);
        if (course == null) return "redirect:/course/not-found";

        String time = String.valueOf(System.currentTimeMillis());
        String orderId = "MOMO" + time;
        String requestId = orderId + "001";

        // Loại bỏ dấu tiếng Việt khỏi tên khóa học
        String courseName = course.getName();
        String normalized = Normalizer.normalize(courseName, Normalizer.Form.NFD);
        String courseNameNoAccent = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(normalized).replaceAll("");

        String orderInfo = "Thanh toan khoa hoc " + courseNameNoAccent;
        String amount = String.valueOf(course.getPrice().intValue());

        // Mã hóa userId và courseId vào extraData
        String extraData = Base64.getEncoder().encodeToString((courseId + "," + userId)
                .getBytes(StandardCharsets.UTF_8));

        // Tạo request
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(amount);
        paymentRequest.setOrderId(orderId);
        paymentRequest.setOrderInfo(orderInfo);
        paymentRequest.setRequestId(requestId);
        paymentRequest.setExtraData(extraData);

        String response = paymentService.createPayment(paymentRequest);
        JsonNode json = new ObjectMapper().readTree(response);
        String payUrl = json.path("payUrl").asText();

        return (payUrl != null && !payUrl.isEmpty()) ? "redirect:" + payUrl : "redirect:/thanks";
    }

    @GetMapping("/thanks")
    public String handleMomoReturn(Model model,
                                   @RequestParam(value = "orderId", required = false) String orderId,
                                   @RequestParam(value = "resultCode", required = false) Integer resultCode) throws Exception {
                                
        String requestId = orderId + "001"; // giống lúc tạo request
                                
        String statusResponse = paymentService.queryTransactionStatus(orderId, requestId);
        JsonNode json = new ObjectMapper().readTree(statusResponse);
                                
        String encodedExtraData = json.path("extraData").asText();
        String decoded = new String(Base64.getDecoder().decode(encodedExtraData), StandardCharsets.UTF_8);
        String[] parts = decoded.split(",");
        Long courseId = Long.parseLong(parts[0]);
        Long userId = Long.parseLong(parts[1]);
                                
        Course course = courseService.getCourseById(courseId);
        model.addAttribute("course", course); // luôn truyền về để hiển thị
                                
        int resultCodeFromApi = json.path("resultCode").asInt();
                                
        if (resultCode != null && resultCode != 0) {
            return "client/thank/failure"; // thất bại từ callback đầu tiên
        }
    
        if (resultCodeFromApi == 0) {
            User user = userService.getUserById(userId);
        
            if (course != null && user != null &&
                !purchaseService.checkPurchaseStudentIdAndCourseId(userId, courseId)) {
                
                Purchase purchase = new Purchase();
                purchase.setCourse(course);
                purchase.setStudent(user);
                purchaseService.handleSavePurchase(purchase);
            }
        
            return "client/thank/thank"; // thành công
        }
    
        return "client/thank/failure"; // thất bại từ kết quả truy vấn
    }
    

    @GetMapping("/test/thank")
    public String getTestThank(Model model) {
        Course course=this.courseService.getCourseById(1);
        model.addAttribute("course", course);
        return "client/thank/thank";
    }
    

    @GetMapping("/test/false")
    public String getTestThankfalse(Model model) {
        Course course=this.courseService.getCourseById(1);
        model.addAttribute("course", course);
        return "client/thank/failure";
    }
    

}

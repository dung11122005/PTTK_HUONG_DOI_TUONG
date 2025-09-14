package com.example.exam_portal.controller.admin;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.exam_portal.domain.ActivityLog;
import com.example.exam_portal.domain.Course;
import com.example.exam_portal.domain.ExamResult;
import com.example.exam_portal.domain.Purchase;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.domain.dto.ResultPaginationDTO;
import com.example.exam_portal.domain.response.ExamResultDTO;
import com.example.exam_portal.domain.response.ExamResultListResponse;
import com.example.exam_portal.domain.response.ResUserDTO;
import com.example.exam_portal.domain.response.RestResponse;
import com.example.exam_portal.domain.response.SoldCourseResponse;
import com.example.exam_portal.service.ActivityLogService;
import com.example.exam_portal.service.CourseService;
import com.example.exam_portal.service.ExamResultService;
import com.example.exam_portal.service.PurchaseService;
import com.example.exam_portal.service.UploadService;
import com.example.exam_portal.service.UserService;
import com.example.exam_portal.spec.SpecificationBuilder;
import com.example.exam_portal.util.annotation.ApiMessage;
import com.example.exam_portal.util.error.IdInvalidException;


@RestController
@RequestMapping("/api/v1")
public class ListTestApiController {

    private final ActivityLogService activityLogService;
    private final ExamResultService examResultService;
    private final UserService userService;
    private final UploadService uploadService;
    private final CourseService courseService;
    private final PurchaseService purchaseService;

    public ListTestApiController(ExamResultService examResultService, 
    ActivityLogService activityLogService, UserService userService, 
    UploadService uploadService, CourseService courseService,
    PurchaseService purchaseService){
        this.examResultService=examResultService;
        this.activityLogService = activityLogService;
        this.userService=userService;
        this.uploadService=uploadService;
        this.courseService=courseService;
        this.purchaseService=purchaseService;
    }


    @PostMapping("/listresult/{examSessionId}")
    @ApiMessage("Fetch exam result list")
    public ResponseEntity<RestResponse<ExamResultListResponse>> getExamResultList(@PathVariable("examSessionId") long id) throws IdInvalidException{
        List<ExamResult> examResults = examResultService.getAllExamResultSessionId(id);

        if (examResults == null || examResults.isEmpty()) {
            throw new IdInvalidException("ExamSession với id = " + id + " không tồn tại hoặc không có kết quả nào.");
        }
    
        // Lấy examName từ 1 kết quả bất kỳ vì cùng kỳ thi
        String examName = examResults.get(0).getExam().getName();

        // Mapping sang DTO
        List<ExamResultDTO> dtos = examResults.stream().map(result -> {
            ExamResultDTO dto = new ExamResultDTO();
            dto.setStudentId(result.getStudent().getId());
            dto.setStudentName(result.getStudent().getFullName());
            dto.setScore(result.getScore());
            dto.setSubmittedAt(result.getSubmittedAt());
            return dto;
        }).collect(Collectors.toList());

        // Gộp vào wrapper response
        ExamResultListResponse data = new ExamResultListResponse();
        data.setExamName(examName);
        data.setResults(dtos);

        // Bọc vào RestResponse
        RestResponse<ExamResultListResponse> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Lấy danh sách kết quả thành công.");
        res.setData(data);
        res.setError(null);

        return ResponseEntity.ok(res);
    }
    

    @GetMapping("/activity-logs")
    public ResponseEntity<ResultPaginationDTO> getAllLogs(
        @RequestParam Map<String, String> params,
        Pageable pageable) {
            // Bỏ các param không liên quan đến filter
        Map<String, String> filteredParams = params.entrySet().stream()
        .filter(e -> !List.of("page", "size", "sort").contains(e.getKey()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Specification<ActivityLog> spec = new SpecificationBuilder<ActivityLog>().buildFromParams(filteredParams);
        return ResponseEntity.ok(activityLogService.fetchAllLogs(spec, pageable));
    }

    @GetMapping("/user/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id,
    @AuthenticationPrincipal Object principal) throws IdInvalidException{
        String email = null;

        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername(); // login thường
        } else if (principal instanceof OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email"); // login bằng Google
        }
        User user = this.userService.getUserByEmail(email);
        User fetchUser = this.userService.getUserById(id);
        if (fetchUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }

        if (user == null || (Long) user.getId()!=id) {
            throw new IdInvalidException("Không thể truy cập tài khoản người khác");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.userService.convertToResUserDTO(fetchUser));
    }

    @PutMapping(value = "/user/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage("update user by id")
    public ResponseEntity<?> updateUser(
            @PathVariable("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
            @AuthenticationPrincipal Object principal
    ) throws IdInvalidException {

        String email="";
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername(); // login thường
        } else if (principal instanceof OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email"); // login bằng Google
        }
        User us = this.userService.getUserByEmail(email);
        if (us == null || (Long) us.getId()!=id) {
            throw new IdInvalidException("Không thể truy cập tài khoản người khác");
        }

        User user = userService.getUserById(id);
        if (user == null) {
            throw new IdInvalidException("Không tìm thấy user với id = " + id);
        }

        // Cập nhật dữ liệu text
        user.setFullName(name);
        user.setPhone(phone);
        user.setAddress(address);
        
        // Xử lý ảnh nếu có
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String savedFileName = this.uploadService.handleSaveUploadFile(avatarFile, "avatars");
            user.setAvatar(savedFileName);
        }

        this.userService.handleSaveUser(user); // hoặc repository.save(user)

        return ResponseEntity.ok(userService.convertToResUserDTO(user));
    }


    @GetMapping("/soldcourse")
    public ResponseEntity<RestResponse<List<SoldCourseResponse>>> getSoldCourse(@AuthenticationPrincipal UserDetails userDetails) throws IdInvalidException {
        User teacher = this.userService.getUserByEmail(userDetails.getUsername());

        if(teacher==null){
            throw new IdInvalidException("bạn chưa đăng nhập, vui lòng đăng nhập");
        }

        boolean isStudent = teacher.getRoles().stream()
        .anyMatch(role -> role.getName().equalsIgnoreCase("STUDENT"));
        if (isStudent) {
            throw new IdInvalidException("bạn không đủ quyền hạn truy cập");
        }
        
        boolean isAdmin = teacher.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));
        
        List<Purchase> purchases;
        List<Course> courses = this.courseService.getCourseByTeacherId(teacher.getId());
        
        if (isAdmin) {
            purchases = this.purchaseService.getAllCourseSold();
        } else {
            purchases = this.purchaseService.getAllCourseSoldListCourseId(courses);
        }


         // Convert to SoldCourseResponse
        List<SoldCourseResponse> soldCourseResponses = purchases.stream()
        .map(p -> new SoldCourseResponse(p.getCourse().getId() ,p.getCourse().getName(), p.getCourse().getPrice()))
        .collect(Collectors.toList());


        RestResponse<List<SoldCourseResponse>> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Lấy danh sách kết quả thành công.");
        res.setData(soldCourseResponses);
        res.setError(null);
        
        return ResponseEntity.ok(res);
    }

}

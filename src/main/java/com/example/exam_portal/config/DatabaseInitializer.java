package com.example.exam_portal.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.AcademicYear;
import com.example.exam_portal.domain.Permission;
import com.example.exam_portal.domain.Role;
import com.example.exam_portal.domain.Subject;
import com.example.exam_portal.domain.SubjectDepartment;
import com.example.exam_portal.repository.AcademicYearRepository;
import com.example.exam_portal.repository.GradeRepository;
import com.example.exam_portal.repository.PermissionRepository;
import com.example.exam_portal.repository.RoleRepository;
import com.example.exam_portal.repository.SubjectDepartmentRepository;
import com.example.exam_portal.repository.SubjectRepository;
import com.example.exam_portal.repository.UserRepository;


@Service
public class DatabaseInitializer implements CommandLineRunner{
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubjectRepository subjectRepository;
    private final SubjectDepartmentRepository subjectDepartmentRepository;
    private final AcademicYearRepository academicYearRepository;
    private final GradeRepository gradeRepository;


    public DatabaseInitializer(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            SubjectRepository subjectRepository,
            SubjectDepartmentRepository subjectDepartmentRepository,
            AcademicYearRepository academicYearRepository,
            GradeRepository gradeRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.subjectRepository = subjectRepository;
        this.subjectDepartmentRepository = subjectDepartmentRepository;
        this.academicYearRepository = academicYearRepository;
        this.gradeRepository=gradeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();
        long countSubjects = this.subjectRepository.count();
        long countDepartments = this.subjectDepartmentRepository.count();
        long countYears = this.academicYearRepository.count();
        long countGrades = this.gradeRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();

            // User
            arr.add(new Permission("Get All User", "/admin/user", "GET", "User"));
            arr.add(new Permission("Get User Group", "/admin/user/group/{groupName}", "GET", "User"));
            arr.add(new Permission("Create User", "/admin/user/create", "GET", "User"));
            arr.add(new Permission("Create User", "/admin/user/create", "POST", "User"));
            arr.add(new Permission("Fetch User", "/admin/user/{id}", "GET", "User"));
            arr.add(new Permission("Update User", "/admin/user/update/{id}", "GET", "User"));
            arr.add(new Permission("Update User", "/admin/user/update", "POST", "User"));
            arr.add(new Permission("Delete User", "/admin/user/delete/{id}", "GET", "User"));
            arr.add(new Permission("Delete User", "/admin/user/delete", "POST", "User"));


            // School Year
            arr.add(new Permission("Get All School Year", "/admin/school-year", "GET", "School Year"));
            arr.add(new Permission("Create School Year", "/admin/school-year/create", "GET", "School Year"));
            arr.add(new Permission("Create School Year", "/admin/school-year/create", "POST", "School Year"));
            arr.add(new Permission("Update School Year", "/admin/school-year/update/{id}", "GET", "School Year"));
            arr.add(new Permission("Delete School Year", "/admin/school-year/delete/{id}", "GET", "School Year"));
            arr.add(new Permission("Delete School Year", "/admin/school-year/delete", "POST", "School Year"));


            // Permission
            arr.add(new Permission("Get All Permission", "/admin/permission", "GET", "Permission"));
            arr.add(new Permission("Get Permission Module", "/admin/permission/module/{moduleName}", "GET", "Permission"));
            arr.add(new Permission("Create Permission", "/admin/permission/create", "GET", "Permission"));
            arr.add(new Permission("Create Permission", "/admin/permission/create", "POST", "Permission"));
            arr.add(new Permission("Update Permission", "/admin/permission/update/{id}", "GET", "Permission"));
            arr.add(new Permission("Update Permission", "/admin/permission/update/{id}", "POST", "Permission"));
            arr.add(new Permission("Delete Permission", "/admin/permission/delete/{id}", "GET", "Permission"));
            arr.add(new Permission("Delete Permission", "/admin/permission/delete", "POST", "Permission"));


            // Role
            arr.add(new Permission("Get All Role", "/admin/role", "GET", "Role"));
            arr.add(new Permission("Create Role", "/admin/role/create", "GET", "Role"));
            arr.add(new Permission("Create Role", "/admin/role/create", "POST", "Role"));
            arr.add(new Permission("Update Role", "/admin/role/update/{id}", "GET", "Role"));
            arr.add(new Permission("Update Role", "/admin/role/update/{id}", "POST", "Role"));
            arr.add(new Permission("Delete Role", "/admin/role/delete/{id}", "GET", "Role"));
            arr.add(new Permission("Delete Role", "/admin/role/delete", "POST", "Role"));


            // Subject
            arr.add(new Permission("Get All Subject", "/admin/subject", "GET", "Subject"));
            arr.add(new Permission("Create Subject", "/admin/subject/create", "GET", "Subject"));
            arr.add(new Permission("Create Subject", "/admin/subject/create", "POST", "Subject"));
            arr.add(new Permission("Update Subject", "/admin/subject/update/{id}", "GET", "Subject"));
            arr.add(new Permission("Update Subject", "/admin/subject/update/{id}", "POST", "Subject"));
            arr.add(new Permission("Delete Subject", "/admin/subject/delete/{id}", "GET", "Subject"));
            arr.add(new Permission("Delete Subject", "/admin/subject/delete", "POST", "Subject"));


            // Subject Department
            arr.add(new Permission("Get All Subject Department", "/admin/subject-department", "GET", "Department"));
            arr.add(new Permission("Create Subject Department", "/admin/subject-department/create", "GET", "Department"));
            arr.add(new Permission("Create Subject Department", "/admin/subject-department/create", "POST", "Department"));
            arr.add(new Permission("Update Subject Department", "/admin/subject-department/update/{id}", "GET", "Department"));
            arr.add(new Permission("Update Subject Department", "/admin/subject-department/update/{id}", "POST", "Department"));
            arr.add(new Permission("Delete Subject Department", "/admin/subject-department/delete/{id}", "GET", "Department"));
            arr.add(new Permission("Delete Subject Department", "/admin/subject-department/delete", "POST", "Department"));


            // Email
            arr.add(new Permission("Show Send Mail Form", "/admin/send-mail", "GET", "Email"));
            arr.add(new Permission("Send Email to Class/Student", "/admin/send-mail", "POST", "Email"));
            arr.add(new Permission("Reply Student Email", "/admin/email/send-mail-student", "POST", "Email"));
            arr.add(new Permission("Fetch Emails from Inbox", "/admin/email/fetch", "GET", "Email"));
            arr.add(new Permission("Get All Email", "/admin/email", "GET", "Email"));
            arr.add(new Permission("Fetch Email", "/admin/email/{id}", "GET", "Email"));


            // Grade
            arr.add(new Permission("Get All Grade", "/admin/grade", "GET", "Grade"));
            arr.add(new Permission("Create Grade", "/admin/grade/create", "GET", "Grade"));
            arr.add(new Permission("Create Grade", "/admin/grade/create", "POST", "Grade"));
            arr.add(new Permission("Update Grade", "/admin/grade/update/{id}", "GET", "Grade"));
            arr.add(new Permission("Update Grade", "/admin/grade/update/{id}", "POST", "Grade"));
            arr.add(new Permission("Delete Grade", "/admin/grade/delete/{id}", "GET", "Grade"));
            arr.add(new Permission("Delete Grade", "/admin/grade/delete", "POST", "Grade"));


            // Class
            arr.add(new Permission("Get All Class", "/admin/class", "GET", "Class"));
            arr.add(new Permission("Get Class By Year", "/admin/class/{yearId}", "GET", "Class"));
            arr.add(new Permission("Create Class", "/admin/class/create", "GET", "Class"));
            arr.add(new Permission("Create Class", "/admin/class/create", "POST", "Class"));
            arr.add(new Permission("Update Class", "/admin/class/update/{id}", "GET", "Class"));
            arr.add(new Permission("Update Class", "/admin/class/update/{id}", "POST", "Class"));
            arr.add(new Permission("Delete Class", "/admin/class/delete/{id}", "GET", "Class"));
            arr.add(new Permission("Delete Class", "/admin/class/delete", "POST", "Class"));
            arr.add(new Permission("Add Student to Class (Form)", "/admin/class/student/add/{classId}", "GET", "Class"));
            arr.add(new Permission("Export Class Students to Excel", "/admin/class/student/add/{classId}/export", "GET", "Class"));
            arr.add(new Permission("Add Student to Class (Submit)", "/admin/class/student/add/{classId}", "POST", "Class"));
            arr.add(new Permission("Delete Student from Class", "/admin/class/student/delete/{classId}/{studentId}", "POST", "Class"));


            // Course
            arr.add(new Permission("Get All Course", "/admin/course", "GET", "Course"));
            arr.add(new Permission("Create Course (Form)", "/admin/course/create", "GET", "Course"));
            arr.add(new Permission("Create Course (Submit)", "/admin/course/create", "POST", "Course"));
            arr.add(new Permission("Update Course (Form)", "/admin/course/update/{id}", "GET", "Course"));
            arr.add(new Permission("Update Course (Submit)", "/admin/course/update/{id}", "POST", "Course"));
            arr.add(new Permission("Delete Course", "/admin/course/delete/{id}", "GET", "Course"));
            arr.add(new Permission("Delete Course (Submit)", "/admin/course/delete", "POST", "Course"));
            arr.add(new Permission("Fetch Course", "/admin/course/{id}", "GET", "Course"));
            arr.add(new Permission("Create Chapter to Course", "/admin/course/chapter/create/{courseId}", "POST", "Course"));
            arr.add(new Permission("Delete Chapter from Course", "/admin/course/chapter/delete/{id}", "GET", "Course"));
            arr.add(new Permission("Create Course Lesson", "/admin/course/lesson/create/{chapterId}", "POST", "Course"));
            arr.add(new Permission("Update Course Lesson", "/admin/course/lesson/update/{id}", "POST", "Course"));
            arr.add(new Permission("Delete Course Lesson", "/admin/course/lesson/delete/{id}", "GET", "Course"));


            // Exam Paper
            arr.add(new Permission("Get All Exam", "/admin/exam", "GET", "Exam Paper"));
            arr.add(new Permission("Get Exam Type", "/admin/exam/type/{type}", "GET", "Exam Paper"));
            arr.add(new Permission("Create Exam (Form)", "/admin/exam/create", "GET", "Exam Paper"));
            arr.add(new Permission("Create Exam (Submit)", "/admin/exam/create", "POST", "Exam Paper"));
            arr.add(new Permission("Update Exam (Form)", "/admin/exam/update/{id}", "GET", "Exam Paper"));
            arr.add(new Permission("Update Exam (Submit)", "/admin/exam/update/{id}", "POST", "Exam Paper"));
            arr.add(new Permission("Delete Exam", "/admin/exam/delete/{id}", "GET", "Exam Paper"));
            arr.add(new Permission("Delete Exam (Submit)", "/admin/exam/delete", "POST", "Exam Paper"));
            arr.add(new Permission("Fetch Exam", "/admin/exam/{id}", "GET", "Exam Paper"));
            arr.add(new Permission("Create Exam Question", "/admin/exam/question/create/{id}", "POST", "Exam Paper"));
            arr.add(new Permission("Update Exam Question", "/admin/exam/question/update/{id}", "POST", "Exam Paper"));
            arr.add(new Permission("Delete Exam Question", "/admin/exam/question/delete/{id}", "GET", "Exam Paper"));


            // Examination
            arr.add(new Permission("Get All Examination", "/admin/test", "GET", "Examination"));
            arr.add(new Permission("Get Examination Year", "/admin/test/year/{yearId}", "GET", "Examination"));
            arr.add(new Permission("Fetch Examination", "/admin/test/{id}", "GET", "Examination"));
            arr.add(new Permission("Export Examination Result", "/admin/test/{id}/export", "GET", "Examination"));
            arr.add(new Permission("Create Examination (Form)", "/admin/test/create", "GET", "Examination"));
            arr.add(new Permission("Create Examination (Submit)", "/admin/test/create", "POST", "Examination"));
            arr.add(new Permission("Update Examination (Form)", "/admin/test/update/{id}", "GET", "Examination"));
            arr.add(new Permission("Update Examination (Submit)", "/admin/test/update/{id}", "POST", "Examination"));
            arr.add(new Permission("Delete Examination", "/admin/test/delete/{id}", "GET", "Examination"));
            arr.add(new Permission("Delete Examination (Submit)", "/admin/test/delete", "POST", "Examination"));
            arr.add(new Permission("Fetch Exam Result", "/admin/test/result/{id}", "GET", "Examination"));


            // Dashboard
            arr.add(new Permission("Get Dashboard", "/admin", "GET", "Dashboard"));

            this.permissionRepository.saveAll(arr);
        }


      
        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            // SUPER_ADMIN: full permissions
            Role superAdmin = new Role("SUPER_ADMIN", "Admin thì full permissions");
            superAdmin.setPermissions(new HashSet<>(allPermissions));

            // ACADEMIC_AFFAIRS_OFFICE: lấy permission theo module
            Set<String> aaoModules = Set.of(
                "School Year", "User", "Teacher", "Grade",
                "Subject", "Role", "Department", "Permission",
                "Class", "Examination"
            );
            Set<Permission> aaoPerms = allPermissions.stream()
                    .filter(p -> p.getModule() != null && aaoModules.contains(p.getModule()))
                    .collect(Collectors.toSet());
            Role aao = new Role("ACADEMIC_AFFAIRS_OFFICE", "Quản lý tài khoản, niên khóa, báo cáo, ...");
            aao.setPermissions(aaoPerms);

            // SUBJECT_TEACHER: permission theo module Exam Paper, Email, Course
            Set<String> subjectTeacherModules = Set.of("Exam Paper", "Email", "Course");
            Set<Permission> subjectTeacherPerms = allPermissions.stream()
                    .filter(p -> p.getModule() != null && subjectTeacherModules.contains(p.getModule()))
                    .collect(Collectors.toSet());
            Role subjectTeacher = new Role("SUBJECT_TEACHER", "Quản lý đề, bài giảng, theo dõi kết quả");
            subjectTeacher.setPermissions(subjectTeacherPerms);

            // Các role khác chỉ tạo, chưa gán permission
            Role vice = new Role("VICE_PRINCIPAL", "Phó hiệu trưởng: xem báo cáo, giám sát hoạt động");
            Role subjectDept = new Role("SUBJECT_DEPARTMENT", "Quản lý đề kiểm tra bộ môn, đề xuất cho phòng giáo vụ");
            Role homeroom = new Role("HOMEROOM_TEACHER", "Giáo viên chủ nhiệm: quản lý lớp, gửi thông báo");
            Role student = new Role("STUDENT", "Học sinh: tham gia học, làm kiểm tra, xem kết quả");

            this.roleRepository.saveAll(List.of(
                superAdmin, aao, vice, subjectDept, subjectTeacher, homeroom, student
            ));
        }
 
        if (countSubjects == 0) {
            List<Subject> subjects = new ArrayList<>();
            Subject s;

            s = new Subject(); s.setCode("MATH"); s.setName("Toán"); subjects.add(s);
            s = new Subject(); s.setCode("LIT");  s.setName("Ngữ văn"); subjects.add(s);
            s = new Subject(); s.setCode("ENG");  s.setName("Tiếng Anh"); subjects.add(s);
            s = new Subject(); s.setCode("PHY");  s.setName("Vật lý"); subjects.add(s);
            s = new Subject(); s.setCode("CHEM"); s.setName("Hóa học"); subjects.add(s);
            s = new Subject(); s.setCode("BIO");  s.setName("Sinh học"); subjects.add(s);
            s = new Subject(); s.setCode("HIS");  s.setName("Lịch sử"); subjects.add(s);
            s = new Subject(); s.setCode("GEO");  s.setName("Địa lý"); subjects.add(s);
            s = new Subject(); s.setCode("CIV");  s.setName("Giáo dục công dân"); subjects.add(s);
            s = new Subject(); s.setCode("IT");   s.setName("Tin học"); subjects.add(s);
            s = new Subject(); s.setCode("PE");   s.setName("Thể dục"); subjects.add(s);
            s = new Subject(); s.setCode("DEF");  s.setName("Giáo dục Quốc phòng"); subjects.add(s);
            s = new Subject(); s.setCode("TECH"); s.setName("Công nghệ"); subjects.add(s);

            List<Subject> savedSubjects = this.subjectRepository.saveAll(subjects);

            // Tạo SubjectDepartment (Tổ bộ môn) nếu chưa có
            if (countDepartments == 0) {
                List<SubjectDepartment> depts = new ArrayList<>();
                SubjectDepartment d;

                // helper để tìm Subject theo code
                java.util.function.Function<String, Subject> findByCode = code ->
                    savedSubjects.stream().filter(x -> code.equals(x.getCode())).findFirst().orElse(null);

                d = new SubjectDepartment(); d.setName("Tổ Toán"); d.setSubject(findByCode.apply("MATH")); depts.add(d);
                d = new SubjectDepartment(); d.setName("Tổ Văn"); d.setSubject(findByCode.apply("LIT")); depts.add(d);
                d = new SubjectDepartment(); d.setName("Tổ Tiếng Anh"); d.setSubject(findByCode.apply("ENG")); depts.add(d);
                d = new SubjectDepartment(); d.setName("Tổ Vật lý"); d.setSubject(findByCode.apply("PHY")); depts.add(d);
                d = new SubjectDepartment(); d.setName("Tổ Hóa học"); d.setSubject(findByCode.apply("CHEM")); depts.add(d);
                d = new SubjectDepartment(); d.setName("Tổ Sinh học"); d.setSubject(findByCode.apply("BIO")); depts.add(d);
                d = new SubjectDepartment(); d.setName("Tổ Lịch sử"); d.setSubject(findByCode.apply("HIS")); depts.add(d);
                d = new SubjectDepartment(); d.setName("Tổ Địa lý"); d.setSubject(findByCode.apply("GEO")); depts.add(d);
                d = new SubjectDepartment(); d.setName("Tổ Giáo dục công dân"); d.setSubject(findByCode.apply("CIV")); depts.add(d);
                d = new SubjectDepartment(); d.setName("Tổ Tin học"); d.setSubject(findByCode.apply("IT")); depts.add(d);
                d = new SubjectDepartment(); d.setName("Tổ Thể dục"); d.setSubject(findByCode.apply("PE")); depts.add(d);
                d = new SubjectDepartment(); d.setName("Tổ Giáo dục Quốc phòng"); d.setSubject(findByCode.apply("DEF")); depts.add(d);
                d = new SubjectDepartment(); d.setName("Tổ Công nghệ"); d.setSubject(findByCode.apply("TECH")); depts.add(d);

                this.subjectDepartmentRepository.saveAll(depts);
            }
        }

        if (countYears == 0) {
            List<AcademicYear> years = new ArrayList<>();
            AcademicYear y;

            y = new AcademicYear(); y.setName("2015-2016"); y.setStartDate(LocalDate.of(2015,8,1)); y.setEndDate(LocalDate.of(2016,5,31)); years.add(y);
            y = new AcademicYear(); y.setName("2016-2017"); y.setStartDate(LocalDate.of(2016,8,1)); y.setEndDate(LocalDate.of(2017,5,31)); years.add(y);
            y = new AcademicYear(); y.setName("2017-2018"); y.setStartDate(LocalDate.of(2017,8,1)); y.setEndDate(LocalDate.of(2018,5,31)); years.add(y);
            y = new AcademicYear(); y.setName("2018-2019"); y.setStartDate(LocalDate.of(2018,8,1)); y.setEndDate(LocalDate.of(2019,5,31)); years.add(y);
            y = new AcademicYear(); y.setName("2019-2020"); y.setStartDate(LocalDate.of(2019,8,1)); y.setEndDate(LocalDate.of(2020,5,31)); years.add(y);
            y = new AcademicYear(); y.setName("2020-2021"); y.setStartDate(LocalDate.of(2020,8,1)); y.setEndDate(LocalDate.of(2021,5,31)); years.add(y);
            y = new AcademicYear(); y.setName("2021-2022"); y.setStartDate(LocalDate.of(2021,8,1)); y.setEndDate(LocalDate.of(2022,5,31)); years.add(y);
            y = new AcademicYear(); y.setName("2022-2023"); y.setStartDate(LocalDate.of(2022,8,1)); y.setEndDate(LocalDate.of(2023,5,31)); years.add(y);
            y = new AcademicYear(); y.setName("2023-2024"); y.setStartDate(LocalDate.of(2023,8,1)); y.setEndDate(LocalDate.of(2024,5,31)); years.add(y);
            y = new AcademicYear(); y.setName("2024-2025"); y.setStartDate(LocalDate.of(2024,8,1)); y.setEndDate(LocalDate.of(2025,5,31)); years.add(y);
            y = new AcademicYear(); y.setName("2025-2026"); y.setStartDate(LocalDate.of(2025,8,1)); y.setEndDate(LocalDate.of(2026,5,31)); years.add(y);

            this.academicYearRepository.saveAll(years);
        }

        
        if (countGrades == 0) {
            List<com.example.exam_portal.domain.Grade> grades = new ArrayList<>();
            com.example.exam_portal.domain.Grade g;
            g = new com.example.exam_portal.domain.Grade(); g.setName("10"); grades.add(g);
            g = new com.example.exam_portal.domain.Grade(); g.setName("11"); grades.add(g);
            g = new com.example.exam_portal.domain.Grade(); g.setName("12"); grades.add(g);
            this.gradeRepository.saveAll(grades);
        }

        // if (countUsers == 0) {
        //     User adminUser = new User();
        //     adminUser.setEmail("admin@gmail.com");
        //     adminUser.setAddress("hn");
        //     adminUser.setFullName("I'm super admin");
        //     adminUser.setPassword(this.passwordEncoder.encode("123456"));

        //     Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
        //     if (adminRole != null) {
        //         adminUser.getRoles().add(adminRole);
        //     }

        //     this.userRepository.save(adminUser);
        // }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else
            System.out.println(">>> END INIT DATABASE");
    }
}

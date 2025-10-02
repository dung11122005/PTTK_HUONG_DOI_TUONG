package com.example.exam_portal.config;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.Permission;
import com.example.exam_portal.repository.PermissionRepository;
import com.example.exam_portal.repository.RoleRepository;
import com.example.exam_portal.repository.UserRepository;


@Service
public class DatabaseInitializer implements CommandLineRunner{
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();

            // User
            arr.add(new Permission("Get All User", "/admin/user", "GET", "User"));
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

        // if (countRoles == 0) {
        //     List<Permission> allPermissions = this.permissionRepository.findAll();

        //     Role adminRole = new Role();
        //     adminRole.setName("SUPER_ADMIN");
        //     adminRole.setDescription("Admin thì full permissions");
        //     adminRole.setPermissions(Set.copyOf(allPermissions));

        //     this.roleRepository.save(adminRole);
        // }

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

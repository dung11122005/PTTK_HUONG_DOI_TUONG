package com.example.exam_portal.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.ClassRoom;
import com.example.exam_portal.domain.ClassStudent;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.repository.ClassRepository;
import com.example.exam_portal.repository.ClassStudentRepository;

import jakarta.transaction.Transactional;

@Service
public class ClassService {
    
    private final ClassRepository classRepository;
    private final ClassStudentRepository classStudentRepository;

    public ClassService(ClassRepository classRepository, ClassStudentRepository classStudentRepository){
        this.classRepository=classRepository;
        this.classStudentRepository=classStudentRepository;
    }

    public Page<ClassRoom> getAllClassRoomPaginationByIdTeacher(Long id, Pageable page) {
        return this.classRepository.findByAssignments_Teacher_Id(id, page);
    }

    public Page<ClassRoom> getAllClassRoomPagination(Pageable page) {
        return this.classRepository.findAll(page);
    }

    public Page<ClassRoom> getAllClassRoomPaginationteId(Long id, Pageable page) {
        return this.classRepository.findByHomeroomTeacherId(id, page);
    }

    public Page<ClassStudent> getAllClassRoomPagination(Specification<ClassStudent> spec, Pageable pageable) {
        return this.classStudentRepository.findAll(spec, pageable);
    }

    public List<ClassRoom> getAllClassRoom() {
        return this.classRepository.findAll();
    }

    public ClassRoom handleSaveClassRoom(ClassRoom classrRoom) {
        ClassRoom eric = this.classRepository.save(classrRoom);
        return eric;
    }

    public ClassRoom getClassRoomById(long id) {
        return this.classRepository.findById(id);
    }

    public List<ClassStudent> getClassStudentById(long id) {
        return this.classStudentRepository.findByClassroom_Id(id);
    }

    public List<User> getClassStudentByListClass(List<ClassRoom> classRooms) {
        List<ClassStudent> classStudents = this.classStudentRepository.findByClassroomIn(classRooms);
    
        return classStudents.stream()
                .map(ClassStudent::getStudent) // lấy User từ ClassStudent
                .collect(Collectors.toList());
    }


    public List<ClassRoom> getClassByTeacherId(long id) {
        return this.classRepository.findByAssignments_Teacher_Id(id);
    }

    public List<ClassStudent> getClassRoomByClassIdAndStudentId(Long classId, Long studentId) {
        return this.classStudentRepository.findByClassroom_IdAndStudent_Id(classId, studentId);
    }

    public void deleteAClassRoom(long id) {
        this.classRepository.deleteById(id);
    }

    @Transactional
    public void deleteAClassAndStudent(Long classId, Long studentId) {
        this.classStudentRepository.deleteByClassroom_IdAndStudent_Id(classId, studentId);
    }

    public void writeExcelFileClassStudent(List<ClassStudent> classStudents, OutputStream outputStream) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Class student");

        // Dòng 0: tên giáo viên (lấy từ dòng đầu tiên của danh sách)
        if (!classStudents.isEmpty()) {
            ClassRoom classes = classStudents.get(0).getClassroom();
            // String teacherName = classes.getTeacher().getFullName();
            String className = classes.getName();

            // Dòng 0: Tên lớp
            Row classRow = sheet.createRow(0);
            classRow.createCell(0).setCellValue("Tên lớp học:");
            classRow.createCell(1).setCellValue(className);

            // Dòng 1: Tên giáo viên
            Row teacherRow = sheet.createRow(1);
            teacherRow.createCell(0).setCellValue("Giáo viên:");
            // teacherRow.createCell(1).setCellValue(teacherName);
        }

        // Tiêu đề bắt đầu từ dòng 3
        Row headerRow = sheet.createRow(2);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Tên học sinh");
        headerRow.createCell(2).setCellValue("email");
        

        // Dữ liệu bắt đầu từ dòng 4
        int rowNum = 3;
        for (ClassStudent classStudent : classStudents) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(classStudent.getId());
            row.createCell(1).setCellValue(classStudent.getStudent().getFullName());
            row.createCell(2).setCellValue(classStudent.getStudent().getEmail());
           
        }

        workbook.write(outputStream);
        workbook.close();
    }
    
}

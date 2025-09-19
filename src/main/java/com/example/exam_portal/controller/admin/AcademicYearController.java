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

import com.example.exam_portal.domain.AcademicYear;
import com.example.exam_portal.service.AcademicYearService;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class AcademicYearController {
    private final AcademicYearService academicYearService;

    public AcademicYearController( AcademicYearService academicYearService){
        this.academicYearService=academicYearService;
    }

    @GetMapping("/admin/school-year")
    public String getUserPage(Model model, @RequestParam("page") Optional<String> pageOptional) {
        int page = 1;
        try {
            if (pageOptional.isPresent()) {
                page = Integer.parseInt(pageOptional.get());
            } else {
                page = 1;
            }
        } catch (Exception e) {

        }
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<AcademicYear> academicYear = this.academicYearService.getAllAcademicYearPagination(pageable);
        List<AcademicYear> academicYears = academicYear.getContent();
        model.addAttribute("academicYear", academicYears);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", academicYear.getTotalPages());
        return "admin/schoolyear/show";
    }

    @GetMapping("/admin/school-year/create")
    public String getCreateAcademicYearPage(Model model) {
        model.addAttribute("newAcademicYear", new AcademicYear());
        return "admin/schoolyear/create";
    }


    @PostMapping("/admin/school-year/create")
    public String createSchoolYear(@ModelAttribute("newAcademicYear") AcademicYear newAcademicYear) {

        this.academicYearService.handleSaveAcademicYear(newAcademicYear);
        return "redirect:/admin/school-year";
    }

    @GetMapping("/admin/school-year/update/{id}")
    public String getUpdateAcademicYearPage(Model model, @PathVariable long id) {
       Optional<AcademicYear> academicYear=this.academicYearService.getAcademicYearById(id);
        model.addAttribute("newAcademicYear", academicYear.get());
        return "admin/schoolyear/update";
    }

    @PostMapping("/admin/school-year/update/{id}")
    public String updateSchoolYear(
            @PathVariable Long id,
            @ModelAttribute("newAcademicYear") AcademicYear updatedAcademicYear
    ) {
        // lấy academic year cũ từ DB
        Optional<AcademicYear> ex = this.academicYearService.getAcademicYearById(id);
        AcademicYear existing=ex.get();
        // cập nhật các field
        existing.setName(updatedAcademicYear.getName());
        existing.setStartDate(updatedAcademicYear.getStartDate());
        existing.setEndDate(updatedAcademicYear.getEndDate());
        existing.setActive(updatedAcademicYear.isActive());

        // lưu lại
        this.academicYearService.handleSaveAcademicYear(existing);

        return "redirect:/admin/school-year";
    }

    @GetMapping("/admin/school-year/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        return "admin/schoolyear/delete";
    }

    @PostMapping("/admin/school-year/delete")
    public String postDeleteUser(@RequestParam("id") Long id, @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {
        this.academicYearService.deleteAcademicYear(id);
        return "redirect:/admin/school-year";
    }


}

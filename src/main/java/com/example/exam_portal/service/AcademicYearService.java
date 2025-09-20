package com.example.exam_portal.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.AcademicYear;
import com.example.exam_portal.repository.AcademicYearRepository;


@Service
public class AcademicYearService {
    private final AcademicYearRepository academicYearRepository;

    public AcademicYearService(AcademicYearRepository academicYearRepository){
        this.academicYearRepository=academicYearRepository;
    }


    public AcademicYear handleSaveAcademicYear(AcademicYear academicYear) {
        AcademicYear year = this.academicYearRepository.save(academicYear);
        return year;
    }


    public Optional<AcademicYear> getAcademicYearById(long id) {
        return this.academicYearRepository.findById(id);
    }

    public void deleteAcademicYear(long id) {
        this.academicYearRepository.deleteById(id);
    }

    public Page<AcademicYear> getAllAcademicYearPagination(Pageable page) {
        return this.academicYearRepository.findAll(page);
    }

    public Page<AcademicYear> getAllAcademicYearPaginationDate(Pageable page) {
        return this.academicYearRepository.findAllByOrderByStartDateDesc(page);
    }

}

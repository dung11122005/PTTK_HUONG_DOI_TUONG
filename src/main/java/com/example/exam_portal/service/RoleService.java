package com.example.exam_portal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.Role;
import com.example.exam_portal.repository.RoleRepository;


@Service
public class RoleService {
    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository){
        this.roleRepository=roleRepository;
    }

    public List<Role> getAllRole() {
        return this.roleRepository.findAll();
    }

    public Role handleSaveRole(Role role) {
        Role year = this.roleRepository.save(role);
        return year;
    }


    public Optional<Role> getRoleById(long id) {
        return this.roleRepository.findById(id);
    }

    public void deleteRole(long id) {
        this.roleRepository.deleteById(id);
    }

    public Page<Role> getAllRolePagination(Pageable page) {
        return this.roleRepository.findAll(page);
    }
}

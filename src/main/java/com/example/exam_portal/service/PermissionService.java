package com.example.exam_portal.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.Permission;
import com.example.exam_portal.repository.PermissionRepository;



@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;


    public PermissionService(PermissionRepository permissionRepository){
        this.permissionRepository=permissionRepository;
    }

    public List<Permission> getAllPermission() {
        return this.permissionRepository.findAll();
    }

    public Page<Permission> getPermissionByModule(String module, Pageable pageable) {
        return this.permissionRepository.findByModule(module, pageable);
    }

    public Permission handleSavePermission(Permission Permission) {
        Permission year = this.permissionRepository.save(Permission);
        return year;
    }


    public Optional<Permission> getPermissionById(long id) {
        return this.permissionRepository.findById(id);
    }

    public void deletePermission(long id) {
        this.permissionRepository.deleteById(id);
    }

    public Page<Permission> getAllPermissionPagination(Pageable page) {
        return this.permissionRepository.findAll(page);
    }

    public Set<Permission> getAllPermissionByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(permissionRepository.findByIdIn(ids));
    }
}

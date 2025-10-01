package com.example.exam_portal.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exam_portal.domain.Permission;
import com.example.exam_portal.service.PermissionService;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    //  Lấy tất cả permissions
    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermission());
    }

    //  Lấy permissions có phân trang
    @GetMapping("/page")
    public ResponseEntity<Page<Permission>> getAllPermissionPagination(Pageable pageable) {
        return ResponseEntity.ok(permissionService.getAllPermissionPagination(pageable));
    }

    //  Lấy chi tiết 1 permission
    @GetMapping("/{id}")
    public ResponseEntity<Permission> getPermissionById(@PathVariable long id) {
        Optional<Permission> permissionOpt = permissionService.getPermissionById(id);
        return permissionOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //  Tạo permission mới
    @PostMapping
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) {
        Permission saved = permissionService.handleSavePermission(permission);
        return ResponseEntity.ok(saved);
    }

    //  Cập nhật permission
    @PutMapping("/{id}")
    public ResponseEntity<Permission> updatePermission(@PathVariable long id, @RequestBody Permission permission) {
        permission.setId(id);
        Permission updated = permissionService.handleSavePermission(permission);
        return ResponseEntity.ok(updated);
    }

    //  Xóa permission
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}

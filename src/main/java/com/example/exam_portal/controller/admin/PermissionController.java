package com.example.exam_portal.controller.admin;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exam_portal.domain.Permission;
import com.example.exam_portal.service.PermissionService;

@Controller
public class PermissionController {
    
    private PermissionService permissionService;

    public PermissionController(PermissionService permissionService){
        this.permissionService=permissionService;
    }


    @GetMapping("/admin/permission")
    public String getModuleListPage(Model model) {
        // Lấy toàn bộ permissions
        List<Permission> permissions = this.permissionService.getAllPermission();
    
        // Lấy danh sách module duy nhất
        Set<String> modules = permissions.stream()
                .map(Permission::getModule)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    
        model.addAttribute("modules", modules);
        return "admin/permission/modules";
    }



    @GetMapping("/admin/permission/module/{moduleName}")
    public String getPermissionByModule(@PathVariable("moduleName") String moduleName,
                                        Model model,
                                        @RequestParam(value = "page", defaultValue = "1") int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<Permission> pe = permissionService.getPermissionByModule(moduleName, pageable);
                                        
        model.addAttribute("moduleName", moduleName);
        model.addAttribute("permissison", pe.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pe.getTotalPages());
        return "admin/permission/detail";
    }


    @GetMapping("/admin/permission/create")
    public String getCreateAcademicYearPage(Model model) {
        model.addAttribute("newPermission", new Permission());
        return "admin/permission/create";
    }

    @PostMapping("/admin/permission/create")
    public String createPermission(@ModelAttribute("newPermission") Permission permission) {

        this.permissionService.handleSavePermission(permission);
        return "redirect:/admin/permission";
    }

    @GetMapping("/admin/permission/update/{id}")
    public String getUpdatePermissionPage(Model model, @PathVariable long id) {
        Optional<Permission> permission = this.permissionService.getPermissionById(id);
        model.addAttribute("newPermission", permission.get());
        return "admin/permission/update";
    }

    @PostMapping("/admin/permission/update/{id}")
    public String updatePermission(
            @PathVariable Long id,
            @ModelAttribute("newPermission") Permission updatedPermission
    ) {
        Optional<Permission> existing = permissionService.getPermissionById(id);
        if(existing.isPresent()){
            Permission p = existing.get();
            p.setName(updatedPermission.getName());
            p.setEndpoint(updatedPermission.getEndpoint());
            p.setMethod(updatedPermission.getMethod());
            p.setModule(updatedPermission.getModule());
            this.permissionService.handleSavePermission(p);  // cần thêm method savePermission trong service
        }
        return "redirect:/admin/permission";
    }


    @GetMapping("/admin/permission/delete/{id}")
    public String getDeletePermissionPage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        return "admin/permission/delete";
    }
    
    @PostMapping("/admin/permission/delete")
    public String postDeletePermission(@RequestParam("id") Long id) {
        this.permissionService.deletePermission(id);
        return "redirect:/admin/permission";
    }

    
}

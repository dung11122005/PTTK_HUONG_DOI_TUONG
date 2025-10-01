package com.example.exam_portal.controller.admin;

import java.util.List;
import java.util.Map;
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
import com.example.exam_portal.domain.Role;
import com.example.exam_portal.service.PermissionService;
import com.example.exam_portal.service.RoleService;


@Controller
public class RoleController {
    private final RoleService roleService;
    private final PermissionService permissionService;


    public RoleController(RoleService roleService, PermissionService permissionService){
        this.roleService=roleService;
        this.permissionService=permissionService;
    }

    @GetMapping("/admin/role")
    public String getPermissionPage(Model model, @RequestParam("page") Optional<String> pageOptional) {
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
        Page<Role> ro = this.roleService.getAllRolePagination(pageable);
        List<Role> roles = ro.getContent();
        model.addAttribute("roles", roles);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ro.getTotalPages());
        return "admin/role/show";
    }

    @GetMapping("/admin/role/create")
    public String showCreateForm(Model model) {
        Role newRole = new Role();
        model.addAttribute("newRole", newRole);

        // Lấy tất cả permission và group theo module
        List<Permission> permissions = this.permissionService.getAllPermission();
        Map<String, List<Permission>> permissionsByModule = permissions.stream()
                .collect(Collectors.groupingBy(Permission::getModule));
        model.addAttribute("permissionsByModule", permissionsByModule);

        return "admin/role/create"; // Thymeleaf template
    }

    @PostMapping("/admin/role/create")
    public String handleCreate(@ModelAttribute("newRole") Role role,
                               @RequestParam(value = "permissionIds", required = false) List<Long> permissionIds) {

        if (permissionIds != null && !permissionIds.isEmpty()) {
            Set<Permission> perms = this.permissionService.getAllPermissionByIds(permissionIds);
            role.setPermissions(perms);
        }

        this.roleService.handleSaveRole(role);
        return "redirect:/admin/role";
    }


    @GetMapping("/admin/role/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Optional<Role> role = this.roleService.getRoleById(id);
        model.addAttribute("role", role.get());

        // Lấy tất cả permission theo module để hiển thị
        List<Permission> permissions = this.permissionService.getAllPermission();
        Map<String, List<Permission>> permissionsByModule = permissions.stream()
                .collect(Collectors.groupingBy(Permission::getModule));
        model.addAttribute("permissionsByModule", permissionsByModule);

        return "admin/role/update"; // file thymeleaf
    }

    @PostMapping("/admin/role/update/{id}")
    public String handleUpdate(@PathVariable Long id,
                               @ModelAttribute("role") Role role,
                               @RequestParam(value = "permissionIds", required = false) List<Long> permissionIds) {

        Optional<Role> existingRole = roleService.getRoleById(id);
        existingRole.get().setName(role.getName());
        existingRole.get().setDescription(role.getDescription());

        if (permissionIds != null && !permissionIds.isEmpty()) {
            Set<Permission> perms = permissionService.getAllPermissionByIds(permissionIds);
            existingRole.get().setPermissions(perms);
        } else {
            existingRole.get().getPermissions().clear();
        }

        this.roleService.handleSaveRole(existingRole.get());

        return "redirect:/admin/role";
    }

    @GetMapping("/admin/role/delete/{id}")
    public String showDeleteForm(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        return "admin/role/delete"; // file thymeleaf
    }

   
    @PostMapping("/admin/role/delete")
    public String handleDelete(@RequestParam("id") Long id) {
        this.roleService.deleteRole(id);
        return "redirect:/admin/role";
    }
}

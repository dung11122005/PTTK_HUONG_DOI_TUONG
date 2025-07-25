package com.example.exam_portal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.Role;
import com.example.exam_portal.domain.User;
import com.example.exam_portal.repository.RoleRepository;
import com.example.exam_portal.repository.UserRepository;


@Service
public class UserService {

    private final UserRepository userRepository;
    private  final RoleRepository roleRepository;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository
            ) {
        this.userRepository = userRepository;
        this.roleRepository=roleRepository;
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public Role getRoleByName(String name) {
        return this.roleRepository.findByName(name);
    }

    public List<User> getUserRoleName(String name) {
        return this.userRepository.findByRole_Name(name);
    }

    public User handleSaveUser(User user) {
        User eric = this.userRepository.save(user);
        return eric;
    }

    public List<User> getAllUser() {
        return this.userRepository.findAll();
    }

    public Page<User> getAllUserPagination(Pageable page) {
        return this.userRepository.findAll(page);
    }

    public User getUserById(long id) {
        return this.userRepository.findById(id);
    }

    public void deleteAUser(long id) {
        this.userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }
}

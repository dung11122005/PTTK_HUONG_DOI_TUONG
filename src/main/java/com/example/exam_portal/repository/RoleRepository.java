package com.example.exam_portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exam_portal.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
    Role findByName(String name);
    
    List<Role> findAllByIdIn(List<Long> ids);

    List<Role> findAllByNameIn(List<String> names);
}

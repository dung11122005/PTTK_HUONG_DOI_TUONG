package com.example.exam_portal.domain.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String email;
    private String password;
    private String phone;
    private String fullName;
    private String address;

    private List<Long> roleIds;     // giữ danh sách id role
    private Long departmentId;
    private Long subjectId;
}

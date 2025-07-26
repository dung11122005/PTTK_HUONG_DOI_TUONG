package com.example.exam_portal.domain.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ResUserDTO {
    private long id;
    private String name;
    private String email;
    private String phone;
    private String avatarUrl;
    private String address;
    private LocalDateTime createdAt;
}

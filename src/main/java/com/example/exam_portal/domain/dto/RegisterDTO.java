package com.example.exam_portal.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterDTO {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String fullName;

    @NotBlank
    private String phone;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;

    public boolean isPasswordConfirmed() {
        return this.password != null && this.password.equals(this.confirmPassword);
    }
}

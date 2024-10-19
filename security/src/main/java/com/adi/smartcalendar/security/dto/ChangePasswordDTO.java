package com.adi.smartcalendar.security.dto;
import lombok.Data;

@Data
public class ChangePasswordDTO {

    private String usernameOrEmail;

    private String oldPassword;

    private String newPassword;

    private String confirmPassword;
}

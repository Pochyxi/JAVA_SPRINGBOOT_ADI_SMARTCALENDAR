package com.adi.smartcalendar.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEmployeeDTO {

    String errorMessage;

    String username;

    String email;

    String name;

    String surname;

    String projectName;
}

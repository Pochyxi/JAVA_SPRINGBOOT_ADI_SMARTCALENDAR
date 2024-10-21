package com.adi.smartcalendar.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDTO {

    private String employeeCode;

    private String name;

    private String surname;

    private LocalDate assumptionDate;

    private LocalDate birthDate;

    private String unilavCode;

    private LocalDate blockedFrom;

    private LocalDate blockedTo;

    private Long userId;

    private int userProfilePower;

    private String userEmail;

    private String projectName;

}

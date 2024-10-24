package com.adi.smartcalendar.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationDetailDTO {

    private String fieldName;

    private String objectName;

    private String errorMessage;
}

package com.adi.smartcalendar.web.dto;

import com.adi.smartcalendar.security.exception.ErrorCodeList;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientDTO {

    private Long id;

    @Size(min = 1, max = 4, message = ErrorCodeList.SIZEERROR)
    @Pattern(regexp = "[A-Z]+", message = ErrorCodeList.UPPERCASEERROR)
    private String name;

}

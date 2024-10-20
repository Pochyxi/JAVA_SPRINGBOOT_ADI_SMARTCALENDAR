package com.adi.smartcalendar.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MassiveGenerationDTO {
    List<UserEmployeeDTO> userEmployeeDTOList;
}

package com.adi.smartcalendar.web.dto.smartMonthPlanDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectEmployeeDTOV2 {

    String projectName;

    List<EmployeeReservationDTOV2> employeeDTOList;

    int reservedPercentage;

}

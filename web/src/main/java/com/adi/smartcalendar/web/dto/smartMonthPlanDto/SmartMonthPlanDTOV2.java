package com.adi.smartcalendar.web.dto.smartMonthPlanDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmartMonthPlanDTOV2 {

    LocalDate timeLimit;

    List<ProjectEmployeeDTOV2> projectEmployeeDTOList;

    List<OfficeInfo> officeInfoList;

    List<ClientInfo> clientInfoList;

    int totalReservedPercentage;

}

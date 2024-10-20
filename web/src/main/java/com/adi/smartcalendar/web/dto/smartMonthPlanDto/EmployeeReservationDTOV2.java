package com.adi.smartcalendar.web.dto.smartMonthPlanDto;

import com.adi.smartcalendar.web.dto.EmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeReservationDTOV2 {

    EmployeeDTO employeeDTO;

    List<CalendarReservation> calendarReservationList;
}

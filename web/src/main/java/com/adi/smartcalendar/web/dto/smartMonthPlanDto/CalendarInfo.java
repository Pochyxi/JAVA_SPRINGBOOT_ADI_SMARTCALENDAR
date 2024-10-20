package com.adi.smartcalendar.web.dto.smartMonthPlanDto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarInfo {

    String calendarDay;

    int reservedWorkspaces;
}

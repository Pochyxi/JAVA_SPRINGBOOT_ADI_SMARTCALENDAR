package com.adi.smartcalendar.web.dto.smartMonthPlanDto;

import com.adi.smartcalendar.web.dto.OfficeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeInfo {

    OfficeDTO officeDTO;

    List<CalendarInfo> calendarInfoList;
}

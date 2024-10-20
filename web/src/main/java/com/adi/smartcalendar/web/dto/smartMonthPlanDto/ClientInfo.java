package com.adi.smartcalendar.web.dto.smartMonthPlanDto;

import com.adi.smartcalendar.web.dto.ClientDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientInfo {

    ClientDTO clientDTO;

    List<CalendarInfo> calendarInfoList;

}

package com.adi.smartcalendar.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarDTO {

    private String id;

    private Integer day;

    private Integer month;

    private Integer year;

    private boolean isHoliday;


}

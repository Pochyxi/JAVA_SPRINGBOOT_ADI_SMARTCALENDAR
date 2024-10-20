package com.adi.smartcalendar.web.service.service;

import com.adi.smartcalendar.web.dto.CalendarDTO;
import com.adi.smartcalendar.web.entity.Calendar;

import java.util.List;

public interface CalendarService {

    //VOID RETURNS
    void createCalendar( CalendarDTO cDTO);

    void save( Calendar calendar);


    //ENTITY RETURNS

    Calendar getCalendarById(String calendarId);

    Calendar mapCalendarDTOToEntity(CalendarDTO cDTO);

    Calendar getCalendarByDayAndMonthAndYear(int day, int month, int year);

    //DTO RETURNS

    CalendarDTO getCalendarDTOById(String calendarId);

    void modifyCalendar(String calendarId, CalendarDTO cDTO);

    CalendarDTO mapCalendarToDTO(Calendar calendar);

    List<CalendarDTO> findByMonthAndYear(int year, int month);
}

package com.adi.smartcalendar.web.service.serviceImpl;

import com.adi.smartcalendar.security.exception.ErrorCodeList;
import com.adi.smartcalendar.security.exception.appException;
import com.adi.smartcalendar.web.dto.CalendarDTO;
import com.adi.smartcalendar.web.entity.Calendar;
import com.adi.smartcalendar.web.repository.CalendarRepository;
import com.adi.smartcalendar.web.service.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;


    @Override
    public void createCalendar( CalendarDTO cDTO) {
        Calendar calendar = mapCalendarDTOToEntity(cDTO);

        save(calendar);
    }

    @Override
    public void save(Calendar calendar) {
        calendarRepository.save(calendar);
    }

    @Override
    public void modifyCalendar(String calendarId, CalendarDTO cDTO) {

        //RECUPERO IL CALENDAR IN BASE ALL'ID
        Calendar calendar = getCalendarById(calendarId);

        //SETTA GIORNO DI FESTA
        if (cDTO.isHoliday()) {
            calendar.setHoliday(true);
        }

        calendarRepository.save(calendar);

    }
    //ENTITY RETURNS

    @Override
    public Calendar getCalendarById(String calendarId) {
        return calendarRepository.findById(calendarId).
                orElseThrow(() -> new appException(HttpStatus.BAD_REQUEST, "CALENDAR " + ErrorCodeList.NF404));
    }

    @Override
    public Calendar mapCalendarDTOToEntity(CalendarDTO cDTO) {
        Calendar calendar = new Calendar();

    if(cDTO.getId()!= null) {
        calendar.setId(cDTO.getId());
    }

    if(cDTO.getDay()!= null) {
        calendar.setDay(cDTO.getDay());
    }

    if(cDTO.getMonth()!= null) {
        calendar.setMonth(cDTO.getMonth());
    }

    if(cDTO.getYear()!= null){
        calendar.setYear(cDTO.getYear());
    }

    if(cDTO.getMonth()!= null){
        calendar.setHoliday(cDTO.isHoliday());


    }


        return calendar;
    }

    @Override
    public Calendar getCalendarByDayAndMonthAndYear(int day, int month, int year) {
        return calendarRepository.findByDayAndMonthAndYear(day, month, year).
                orElseThrow(() -> new appException(HttpStatus.BAD_REQUEST, "CALENDAR " + ErrorCodeList.NF404));


    }

    @Override
    public CalendarDTO getCalendarDTOById(String calendarId) {
        Calendar calendar = getCalendarById(calendarId);
        return mapCalendarToDTO(calendar);
    }





    @Override
    public CalendarDTO mapCalendarToDTO(Calendar calendar) {
        CalendarDTO cDTO = new CalendarDTO();

        cDTO.setId(calendar.getId());

        cDTO.setDay(calendar.getDay());

        cDTO.setMonth(calendar.getMonth());

        cDTO.setYear(calendar.getYear());

        cDTO.setHoliday(calendar.isHoliday());

        return cDTO;
    }

    @Override
    public List<CalendarDTO> findByMonthAndYear(int month, int year) {
        List<Calendar> calendarList = calendarRepository.findByMonthAndYearOrderByDayAsc(month, year);
        return calendarList.stream().map(this::mapCalendarToDTO).toList();
    }


}

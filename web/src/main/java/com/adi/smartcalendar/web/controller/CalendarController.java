package com.adi.smartcalendar.web.controller;

import com.adi.smartcalendar.web.dto.CalendarDTO;
import com.adi.smartcalendar.web.service.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    @Autowired
    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('CALENDAR_UPDATE') ")
    public ResponseEntity<Void> modifyCalendar(@PathVariable String id,
                                                      @RequestBody CalendarDTO cDTO) {
        calendarService.modifyCalendar(id, cDTO);
       return new ResponseEntity<>(HttpStatus.OK);

    }




}

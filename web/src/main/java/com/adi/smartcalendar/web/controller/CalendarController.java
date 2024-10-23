package com.adi.smartcalendar.web.controller;

import com.adi.smartcalendar.web.dto.CalendarDTO;
import com.adi.smartcalendar.web.service.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;


    /**
     * UPDATE CALENDAR, isHoliday settabile
     * @param id id del calendario (il giorno formattato come dd-MM-yyyy)
     * @param cDTO DTO contenente i dati del calendario
     * @return ResponseEntity<Void>
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('CALENDAR_UPDATE') ")
    public ResponseEntity<Void> modifyCalendar(@PathVariable String id,
                                                      @RequestBody CalendarDTO cDTO) {
        calendarService.modifyCalendar(id, cDTO);
       return new ResponseEntity<>(HttpStatus.OK);

    }




}

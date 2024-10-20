package com.adi.smartcalendar.web.controller;

import com.adi.smartcalendar.web.dto.ReservationDTO;
import com.adi.smartcalendar.web.dto.smartMonthPlanDto.MassivePrenotationDTO;
import com.adi.smartcalendar.web.service.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/reservation")
public class ReservationController {


    private final ReservationService reservationService;


    @GetMapping(value = "/project/{projectId}")
    @PreAuthorize("hasAuthority('RESERVATION_READ') ")
    public ResponseEntity<List<ReservationDTO>>getReservationsByProjectId( @PathVariable Long projectId){
        return new ResponseEntity<>(reservationService.getReservationsByProjectId(projectId), HttpStatus.OK);
    }

    @GetMapping(value = "/employee/{employeeId}/month/{month}/year/{year}")
    public ResponseEntity<List<ReservationDTO>> getAllByMonthAndYearAndEmployeeIdOrderByDayAsc(
            @PathVariable Long employeeId, @PathVariable int month, @PathVariable int year
    ) {
        return new ResponseEntity<>(reservationService.getAllByMonthAndYearAndEmployeeIdOrderByDayAsc(month, year, employeeId), HttpStatus.OK);
    }

    @GetMapping(value = "/employee/{employeeId}/calendar/{calendarId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByEmployeeIdAndCalendarId(
            @PathVariable Long employeeId, @PathVariable String calendarId
    ) {
        return new ResponseEntity<>(reservationService.getByEmployeeAndDate(employeeId, calendarId), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Void>createReservation(@RequestBody ReservationDTO reservationDTO){
        reservationService.createReservation(reservationDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/create/massive")
    public ResponseEntity<Void>createMassivePrenotation(@RequestBody MassivePrenotationDTO massivePrenotationDTO){
        reservationService.createMassivePrenotation(massivePrenotationDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/delete/{reservationId}")
    public ResponseEntity<Void>deleteReservation(@PathVariable Long reservationId){
        reservationService.deleteReservation(reservationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PutMapping(value = "/update/{reservationId}")
    @PreAuthorize("hasAuthority('RESERVATION_UPDATE') ")
    public ResponseEntity<Void>modifyReservation(@PathVariable Long reservationId,
                                                 @RequestBody ReservationDTO reservationDTO){
        reservationService.modifyReservation(reservationId,reservationDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

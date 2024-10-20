package com.adi.smartcalendar.web.service.service;

import com.adi.smartcalendar.web.dto.ReservationDTO;
import com.adi.smartcalendar.web.dto.smartMonthPlanDto.MassivePrenotationDTO;
import com.adi.smartcalendar.web.entity.Reservation;

import java.util.List;
import java.util.Set;

public interface ReservationService {

    //VOID RETURNS
    void save( Reservation reservation);

    void createReservation( ReservationDTO reservationDTO);

    void createMassivePrenotation( MassivePrenotationDTO massivePrenotationDTO);

    void modifyReservation(Long reservationId,ReservationDTO reservationDTO);

    void deleteReservation(Long reservationId);

    void deleteReservationByEmployeeIdAndCalendarId(Long employeeId,String calendarId);

    //ENTITY RETURNS

    Reservation getReservationById(Long id);

    //DTO RETURNS
    Set<ReservationDTO> getReservationsByOfficeId(Long officeId);

    Set<ReservationDTO> getReservationsByEmployeeId(Long employeeId);

    List<ReservationDTO> getReservationsByProjectId(Long projectId);

    List<ReservationDTO> findByEmployeeIdAndCalendarMonthAndCalendarDay( Long employee_id, Integer calendar_month, Integer calendar_day );

    List<ReservationDTO> getByEmployeeAndDate(Long employeeId, String calendarId);

    List<ReservationDTO> getAllByMonthAndYearAndEmployeeIdOrderByDayAsc(int month,int year, Long employeeId);

    List<ReservationDTO> getAllByMonthAndYear(int month,int year);



}

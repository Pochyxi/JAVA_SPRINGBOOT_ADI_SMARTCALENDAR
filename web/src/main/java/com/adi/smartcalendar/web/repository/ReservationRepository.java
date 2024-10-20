package com.adi.smartcalendar.web.repository;

import com.adi.smartcalendar.web.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    Set<Reservation> findByEmployeeId(Long employeeId);

    Set<Reservation> findByOfficeId(Long officeId);

    Set<Reservation> findByClientId(Long clientId);

    Optional<Reservation> findByEmployeeIdAndCalendarId(Long employeeId, String calendarId);

    List<Reservation> findByCalendarMonthAndCalendarYearAndEmployeeIdOrderByCalendarDayAsc(int month, int year, Long employeeId);

    List<Reservation> findByCalendarMonthAndCalendarYear(int month, int year);

    List<Reservation> findByEmployeeIdAndCalendarMonthAndCalendarDay( Long employee_id, Integer calendar_month, Integer calendar_day );

   }

package com.adi.smartcalendar.web.repository;

import com.adi.smartcalendar.web.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    Optional<Calendar> findById(String id);

    List<Calendar> findByMonthAndYearOrderByDayAsc(int month, int year);

    Optional<Calendar> findByDayAndMonthAndYear(int day,int month,int year);

}

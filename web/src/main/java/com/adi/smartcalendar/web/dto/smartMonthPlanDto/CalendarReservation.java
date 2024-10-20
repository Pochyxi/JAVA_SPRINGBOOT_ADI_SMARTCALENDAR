package com.adi.smartcalendar.web.dto.smartMonthPlanDto;

import com.adi.smartcalendar.web.dto.ReservationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarReservation {
    String date;
    ReservationDTO reservationDTO;
}

package com.adi.smartcalendar.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {

private Long id;

private Long employeeId;

private String calendarId;

private Long officeId;

private Long clientId;

private String reservationTypeName;

private boolean isSmart;

}

package com.adi.smartcalendar.web.dto.smartMonthPlanDto;

import com.adi.smartcalendar.web.dto.ReservationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MassivePrenotationDTO {
    List<ReservationDTO> reservationDTOList;
}

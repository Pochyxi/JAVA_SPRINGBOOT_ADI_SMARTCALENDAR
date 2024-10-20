package com.adi.smartcalendar.web.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "CALENDARS")
public class Calendar {
    @Id
    private String id;

    private Integer day;

    private Integer month;

    private Integer year;

    private boolean isHoliday;

    @OneToMany(mappedBy="calendar", cascade = CascadeType.ALL)
    private Set<Reservation> reservations;

}

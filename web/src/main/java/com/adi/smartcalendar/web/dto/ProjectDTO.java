package com.adi.smartcalendar.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {

   private Long id;

   private String name;

   private LocalDate blockedFrom;

   private LocalDate blockedTo;



}

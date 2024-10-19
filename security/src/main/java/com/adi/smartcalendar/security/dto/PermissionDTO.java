package com.adi.smartcalendar.security.dto;

import com.adi.smartcalendar.security.enumerated.PermissionList;
import lombok.Data;

@Data
public class PermissionDTO {

  private Long id;

  private PermissionList name;

}

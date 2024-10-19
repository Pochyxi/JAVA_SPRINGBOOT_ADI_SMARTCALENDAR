package com.adi.smartcalendar.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponseDTO {

    private String accessToken;

    private String tokenType = "Bearer";

    private UserDTO user;
}

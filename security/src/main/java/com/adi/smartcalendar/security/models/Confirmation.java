package com.adi.smartcalendar.security.models;

import com.adi.smartcalendar.security.enumerated.TokenType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class Confirmation {

    private Long id;

    private String token;

    private TokenType tokenType;

    private LocalDateTime createdDate;

    private User user;

}

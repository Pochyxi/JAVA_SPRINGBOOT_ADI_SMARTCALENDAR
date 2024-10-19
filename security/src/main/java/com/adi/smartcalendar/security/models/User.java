package com.adi.smartcalendar.security.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class User {

    private Long id;

    private String username;

    private String email;

    private String password;

    private boolean isTemporaryPassword;

    private boolean isEnabled;

    private LocalDateTime dateTokenCheck;

    private Profile profile;

    private List<Confirmation> confirmation;

}

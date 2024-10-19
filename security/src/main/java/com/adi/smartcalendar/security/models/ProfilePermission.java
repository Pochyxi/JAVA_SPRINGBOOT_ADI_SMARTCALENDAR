package com.adi.smartcalendar.security.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfilePermission {

    private Long id;

    private Profile profile;

    private Permission permission;

    private int valueRead = 0;

    private int valueCreate = 0;

    private int valueUpdate = 0;

    private int valueDelete = 0;

}

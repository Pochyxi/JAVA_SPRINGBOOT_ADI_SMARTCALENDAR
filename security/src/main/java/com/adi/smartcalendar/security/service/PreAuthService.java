package com.adi.smartcalendar.security.service;

public interface PreAuthService {

    boolean userHasPowerOnSubject(Long subjectId, String permissionName );
}

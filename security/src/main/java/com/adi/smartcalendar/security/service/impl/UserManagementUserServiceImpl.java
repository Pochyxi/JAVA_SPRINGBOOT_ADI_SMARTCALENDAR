package com.adi.smartcalendar.security.service.impl;

import com.adi.smartcalendar.security.dto.ChangePasswordDTO;
import com.adi.smartcalendar.security.dto.SignupDTO;
import com.adi.smartcalendar.security.enumerated.TokenType;
import com.adi.smartcalendar.security.service.UserApiService;
import com.adi.smartcalendar.security.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserManagementUserServiceImpl implements UserManagementService {

    private final UserApiService userApiService;

    @Override
    public void signup( SignupDTO signupDTO ) {
        this.userApiService.signup( signupDTO ).block();
    }

    @Override
    public void verifyToken( String token, TokenType tokenType ) {
        userApiService.verifyToken( token, tokenType ).block();
    }

    @Override
    public void changePassword( ChangePasswordDTO changePasswordDTO, String token ) {
        userApiService.changePassword( changePasswordDTO, token ).block();
    }

    @Override
    public void changeEmail( Long userId, String email ) {
        userApiService.changeEmail( userId, email ).block();
    }

    @Override
    public void recoveryPassword( String email ) {
        userApiService.recoveryPassword( email ).block();
    }

    @Override
    public void resendVerificationRequest( Long userId ) {
        userApiService.resendVerificationRequest( userId ).block();
    }
}

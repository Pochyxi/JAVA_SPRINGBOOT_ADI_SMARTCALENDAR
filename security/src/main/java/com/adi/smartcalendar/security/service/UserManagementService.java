package com.adi.smartcalendar.security.service;


import com.adi.smartcalendar.security.dto.ChangePasswordDTO;
import com.adi.smartcalendar.security.dto.SignupDTO;
import com.adi.smartcalendar.security.enumerated.TokenType;

public interface UserManagementService {

    void signup( SignupDTO signupDTO);

    void verifyToken( String token, TokenType tokenType );

    void changePassword( ChangePasswordDTO changePasswordDTO, String token );

    void changeEmail( Long userId, String email );

    void recoveryPassword( String email );

    void resendVerificationRequest( Long userId );
}

package com.adi.smartcalendar.security.service;

import com.adi.smartcalendar.security.dto.*;
import com.adi.smartcalendar.security.enumerated.TokenType;
import com.adi.smartcalendar.security.models.User;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.ForkJoinPool;

public interface UserApiService {
    Mono<UserDTO> getById( Long id);
    Mono<UserDTOInternal> getUserData( String usernameOrEmail);
    Mono<Boolean> existsByUsernameOrEmail(String usernameOrEmail);
    Mono<Set<ProfilePermissionDTO>> getProfilePermissions( Long profileId);
    Mono<Void> signup( SignupDTO signupDTO );
    Mono<ProfileDTO> getProfile( Long userId );
    Mono<PagedResponseDTO<UserDTO>> getAllUsers( int pageNo, int pageSize, String sortBy, String sortDir, int powerOfUser);
    Mono<PagedResponseDTO<UserDTO>> getUsersByEmailContainsIgnoreCase(String email, int pageNo, int pageSize,
                                                                      String sortBy,
                                                                      String sortDir);
    Mono<UserDTO> createUser( SignupDTO signupDTO );
    Mono<UserDTO> modifyUser( Long id, UserDTO userDTO );
    Mono<Void> deleteUser( Long id );
    Mono<Void> verifyToken( String token, TokenType tokenType );
    Mono<Void> changePassword( ChangePasswordDTO changePasswordDTO, String token );
    Mono<Void> changeEmail( Long userId, String email );
    Mono<Void> recoveryPassword( String email );
    Mono<Void> resendVerificationRequest( Long userId );

    Mono<UserDTO> findByEmail( String mail );
}

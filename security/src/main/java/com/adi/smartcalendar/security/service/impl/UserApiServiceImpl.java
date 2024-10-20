package com.adi.smartcalendar.security.service.impl;

import com.adi.smartcalendar.security.dto.*;
import com.adi.smartcalendar.security.enumerated.TokenType;
import com.adi.smartcalendar.security.exception.appException;
import com.adi.smartcalendar.security.service.UserApiService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserApiServiceImpl implements UserApiService {

    private static final Logger logger = LoggerFactory.getLogger( UserApiServiceImpl.class);

    private final String BASE_URI = "/user";

    private final WebClient webClient;

    @Override
    public Mono<UserDTO> getById( Long id) {
        return webClient.get()
                .uri(BASE_URI + "/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError)
                .bodyToMono(UserDTO.class);
    }

    @Override
    public Mono<UserDTOInternal> getUserData( String usernameOrEmail) {
        return webClient.get()
                .uri(BASE_URI + "/username_email/{usernameOrEmail}", usernameOrEmail)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError)
                .bodyToMono(UserDTOInternal.class);
    }

    @Override
    public Mono<Boolean> existsByUsernameOrEmail(String usernameOrEmail) {
        return webClient.get()
                .uri(BASE_URI + "/username_email/exist/{usernameOrEmail}", usernameOrEmail)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError)
                .bodyToMono( Boolean.class);
    }

    public Mono<Set<ProfilePermissionDTO>> getProfilePermissions( Long profileId) {
        return webClient.get()
                .uri(BASE_URI + "/profile_permissions/{profileId}", profileId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError)
                .bodyToFlux( ProfilePermissionDTO.class)
                .collect( Collectors.toSet());
    }

    public Mono<Void> signup( SignupDTO signupDTO ) {
        return webClient.post()
                .uri( BASE_URI + "/signup" )
                .bodyValue( signupDTO )
                .retrieve()
                .onStatus( status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError )
                .bodyToMono( Void.class );
    }

    @Override
    public Mono<ProfileDTO> getProfile( Long userId ) {
        return webClient.get()
                .uri( BASE_URI + "/profile/{userId}", userId )
                .retrieve()
                .onStatus( status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError )
                .bodyToMono( ProfileDTO.class );
    }

    @Override
    public Mono<Void> changePassword( ChangePasswordDTO changePasswordDTO, String token ) {
        return webClient.put()
                .uri( BASE_URI + "/change_password?token={token}", token )
                .bodyValue( changePasswordDTO )
                .retrieve()
                .onStatus( status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError )
                .bodyToMono( Void.class );
    }

    @Override
    public Mono<PagedResponseDTO<UserDTO>> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir,
                                                       int powerOfUser) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URI + "/all")
                        .queryParam("pageNo", pageNo)
                        .queryParam("pageSize", pageSize)
                        .queryParam("sortBy", sortBy)
                        .queryParam("sortOrder", sortDir)
                        .queryParam("powerOfUser", powerOfUser)
                        .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError)
                .bodyToMono( new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public Mono<PagedResponseDTO<UserDTO>> getUsersByEmailContainsIgnoreCase(String email, int pageNo, int pageSize,
                                                                             String sortBy, String sortDir) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URI + "/email_contains/" + email)
                        .queryParam("pageNo", pageNo)
                        .queryParam("pageSize", pageSize)
                        .queryParam("sortBy", sortBy)
                        .queryParam("sortOrder", sortDir)
                        .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError)
                .bodyToMono( new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public Mono<UserDTO> modifyUser( Long id, UserDTO userDTO ) {
        return webClient.put()
                .uri( BASE_URI + "/update/{id}", id )
                .bodyValue( userDTO )
                .retrieve()
                .onStatus( status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError )
                .bodyToMono( UserDTO.class );
    }

    @Override
    public Mono<Void> deleteUser( Long id ) {
        return webClient.delete()
                .uri( BASE_URI + "/delete/{id}", id )
                .retrieve()
                .onStatus( status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError )
                .bodyToMono( Void.class );
    }

    @Override
    public Mono<Void> verifyToken(String token, TokenType tokenType) {
        return webClient.get()
                .uri(uriBuilder -> {
                    // Costruisci il path di base
                    UriBuilder builder = uriBuilder.path(BASE_URI + "/verify");

                    // Aggiungi parametri alla query solo se presenti
                    if (token != null && !token.isEmpty()) {
                        builder.queryParam("token", token);
                    }
                    if (tokenType != null) {
                        builder.queryParam("tokentype", tokenType);
                    }

                    // Costruisci e ritorna l'URI finale
                    return builder.build();
                })
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError)
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Void> changeEmail( Long userId, String email ) {
        return webClient.put()
                .uri( BASE_URI + "/change_email?userId={userId}&email={email}", userId, email )
                .retrieve()
                .onStatus( status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError )
                .bodyToMono( Void.class );
    }

    @Override
    public Mono<Void> recoveryPassword( String email ) {
        return webClient.get()
                .uri( BASE_URI + "/recovery_password?email={email}", email )
                .retrieve()
                .onStatus( status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError )
                .bodyToMono( Void.class );
    }

    @Override
    public Mono<Void> resendVerificationRequest( Long userId ) {
        return webClient.put()
                .uri( BASE_URI + "/resend_verification?userId={userId}", userId )
                .retrieve()
                .onStatus( status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError )
                .bodyToMono( Void.class );
    }

    @Override
    public Mono<UserDTO> createUser( SignupDTO signupDTO ) {
        return webClient.post()
                .uri( BASE_URI + "/create" )
                .bodyValue( signupDTO )
                .retrieve()
                .onStatus( status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError )
                .bodyToMono( UserDTO.class );
    }

    @Override
    public Mono<UserDTO> findByEmail( String email ) {
        return webClient.get()
                .uri( BASE_URI + "/email/{email}", email )
                .retrieve()
                .onStatus( status -> status.is4xxClientError() || status.is5xxServerError(), this::handleError )
                .bodyToMono( UserDTO.class );
    }

    private Mono<? extends Throwable> handleError( ClientResponse clientResponse) {
        return clientResponse.bodyToMono( ErrorDetailsDTO.class)
                .flatMap(errorDetails -> {
                    HttpStatus status = ( HttpStatus ) clientResponse.statusCode();
                    String timestamp = errorDetails.getTimestamp() != null ?
                            errorDetails.getTimestamp().toString() : "N/A";
                    String message = errorDetails.getMessage() != null ?
                            errorDetails.getMessage() : "N/A";
                    String details = errorDetails.getDetails() != null ?
                            errorDetails.getDetails() : "N/A";
                    String errorMessage = String.format("Errore ricevuto: Timestamp: %s, Messaggio: %s, Dettagli: %s", timestamp, message, details);

                    // Logga i dettagli dell'errore
                    logger.error(errorMessage);

                    return Mono.error(new appException(
                            status, message));
                });
    }
}

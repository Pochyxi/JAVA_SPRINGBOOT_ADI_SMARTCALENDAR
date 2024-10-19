package com.adi.smartcalendar.security.exception;

import com.adi.smartcalendar.security.dto.ErrorDetailsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            ResourceNotFoundException.class,
            appException.class,
            Exception.class
    })
    public ResponseEntity<ErrorDetailsDTO> handleResourceNotFoundException( Exception exception,
                                                                            WebRequest webRequest ) {

        switch (exception) {
            // ECCEZIONE DI RISORSA NON TROVATA
            case ResourceNotFoundException resourceNotFoundException -> {
                ErrorDetailsDTO errorDetailsDto = new ErrorDetailsDTO( new Date(), resourceNotFoundException.getMessage(),
                        webRequest.getDescription( false ) );

                return new ResponseEntity<>( errorDetailsDto, HttpStatus.NOT_FOUND );

            }
            // ECCEZIONE GENERICA
            case appException appException -> {
                ErrorDetailsDTO errorDetailsDto = new ErrorDetailsDTO( new Date(), appException.getMessage(),
                        webRequest.getDescription( false ) );

                return new ResponseEntity<>( errorDetailsDto, appException.getStatus() );
            }

            case HttpMessageNotReadableException ex -> {
                ErrorDetailsDTO errorDetailsDto = new ErrorDetailsDTO( new Date(), ex.getMessage(),
                        webRequest.getDescription( false ) );
                return new ResponseEntity<>(errorDetailsDto, HttpStatus.BAD_REQUEST);

            }

            // ECCEZIONE DI VALIDAZIONE DEI CAMPI DI UNA RICHIESTA
            case MethodArgumentNotValidException methodArgumentNotValidException -> {
                ErrorDetailsDTO errorDetailsDto = new ErrorDetailsDTO( new Date(), methodArgumentNotValidException.getMessage(),
                        webRequest.getDescription( false ) );

                return new ResponseEntity<>( errorDetailsDto, HttpStatus.BAD_REQUEST );
            }

            case AuthorizationDeniedException ignored -> {
                ErrorDetailsDTO errorDetailsDto = new ErrorDetailsDTO( new Date(), ErrorCodeList.ACCESSDENIED,
                        webRequest.getDescription( false ) );

                return new ResponseEntity<>( errorDetailsDto, HttpStatus.FORBIDDEN );
            }

            case null, default -> {
                assert exception != null;
                ErrorDetailsDTO errorDetailsDto = new ErrorDetailsDTO( new Date(), exception.getMessage(),
                        webRequest.getDescription( false ) );

                return new ResponseEntity<>( errorDetailsDto, HttpStatus.INTERNAL_SERVER_ERROR );
            }
        }

    }

}

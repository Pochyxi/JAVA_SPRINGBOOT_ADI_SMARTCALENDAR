package com.adi.smartcalendar.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /* COMMENCE
         * Questo metodo gestisce le eccezioni di autenticazione.
         * Prende come input la richiesta, la risposta e l'eccezione di autenticazione.
         * Imposta lo stato della risposta a UNAUTHORIZED (401) e scrive il messaggio dell'eccezione nella risposta.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}

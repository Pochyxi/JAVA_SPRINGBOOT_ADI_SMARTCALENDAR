package com.adi.smartcalendar.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final JwtTokenProvider jwtTokenProvider;

    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter( JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }


    /* DO FILTER INTERNAL
         * Questo metodo viene chiamato per ogni richiesta.
         * Se la richiesta contiene un token valido, viene impostato l'oggetto Authentication nel contesto di Security.
         * In questo modo, Spring Security sa che l'utente è autenticato e può accedere alle risorse protette.
     */
    @Override
    protected void doFilterInternal( HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain )
            throws ServletException, IOException {

        // CREO UNA STRINGA CHE CONTIENE IL TOKEN
        String token = getTokenFromRequest( request );

        // SE LA STRINGA NON E' VUOTA E IL TOKEN E' VALIDO
        if( StringUtils.hasText( token ) && jwtTokenProvider.validateToken( token ) ) {

            // RECUPERO L'USERNAME DALL'OGGETTO TOKEN
            String username = jwtTokenProvider.getUsernameFromToken( token );

            // RECUPERO L'OGGETTO USERDETAILS DALL'OGGETTO USERDETAILSSERVICE
            UserDetails userDetails = userDetailsService.loadUserByUsername( username );

            // CREO UN OGGETTO AUTHENTICATION
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken( userDetails, null,
                            userDetails.getAuthorities() );

            // SETTO L'OGGETTO AUTHENTICATION NEL CONTESTO DI SECURITY
            authenticationToken.setDetails( new WebAuthenticationDetailsSource().buildDetails( request ) );
            SecurityContextHolder.getContext().setAuthentication( authenticationToken );
        }

        // CHIAMO IL METODO DOFILTERINTERNAL
        filterChain.doFilter( request, response );
    }


    /* GET TOKEN FROM REQUEST
         * Questo metodo recupera il token dalla richiesta.
         * Prende come input la richiesta HTTP.
         * Recupera l'header "Authorization" dalla richiesta e verifica se inizia con "Bearer ".
         * Se l'header "Authorization" esiste e inizia con "Bearer ", restituisce il token.
         * Altrimenti, restituisce null.
     */
    private String getTokenFromRequest( HttpServletRequest request ) {

        String bearerToken = request.getHeader( "Authorization" );
        if( StringUtils.hasText( bearerToken ) && bearerToken.startsWith( "Bearer " ) ) {
            return bearerToken.substring( 7 );
        }
        return null;
    }

    private String convertObjectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            // Gestire l'eccezione, potresti voler loggare l'errore o gestirlo in altro modo
            return null;
        }
    }

}

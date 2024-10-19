package com.adi.smartcalendar.security;

import com.adi.smartcalendar.security.dto.UserDTOInternal;
import com.adi.smartcalendar.security.exception.ErrorCodeList;
import com.adi.smartcalendar.security.exception.ResourceNotFoundException;
import com.adi.smartcalendar.security.exception.appException;
import com.adi.smartcalendar.security.service.impl.UserServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Component
public class JwtTokenProvider {

    UserServiceImpl userService;

    private final JwtConfig jwtConfig;

    @Autowired
    public JwtTokenProvider( UserServiceImpl userService, JwtConfig jwtConfig ) {
        this.userService = userService;
        this.jwtConfig = jwtConfig;
    }


    /* VALIDATE TOKEN
     * Questo metodo controlla se il token è valido.
     */
    public boolean validateToken( String authToken ) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey( key() )
                    .build()
                    .parseClaimsJws( authToken )
                    .getBody();


            Date issuedAt = claims.getIssuedAt();
            String username = claims.getSubject();

            LocalDateTime issuedAtLocalDateTime = issuedAt.toInstant()
                    .atZone( ZoneId.systemDefault())
                    .toLocalDateTime();

            UserDTOInternal user = userService.findByUsernameOrEmail(username).orElseThrow(
                    () -> new ResourceNotFoundException( ErrorCodeList.NF404)
            );

            if ( issuedAtLocalDateTime.isBefore(user.getDateTokenCheck())) throw new appException(
                    HttpStatus.BAD_REQUEST, ErrorCodeList.TOKENOBSOLETE
            );



            return true;

        } catch( MalformedJwtException ex ) {
            System.out.println( "Token non valido" );
        } catch( ExpiredJwtException ex ) {
            System.out.println( "Token JWT scaduto" );
        } catch( UnsupportedJwtException ex ) {
            System.out.println( "Token JWT non supportato" );
        } catch( IllegalArgumentException ex ) {
            System.out.println( "Token JWT vuoto" );
        }
        return false;
    }



    /**
     * KEY
     * Questo metodo recupera la chiave segreta dal file properties.
     * La chiave viene convertita in un oggetto Key.
     * @return Oggetto Key
     */
    private Key key() {

        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode( jwtConfig.getJwtSecret() )
        );
    }

    /**
     * Estrae il nome utente o l'email dal token JWT.
     *
     * @param token Token JWT
     * @return Nome utente o email estratto dal token
     */
    public String getUsernameFromToken( String token ) {
        try {

            // Estrae i claims dal token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey( key() )
                    .build()
                    .parseClaimsJws( token )
                    .getBody();

            // Restituisce il subject, che di solito contiene l'username o l'email
            return claims.getSubject();
        } catch( Exception ex ) {
            // In caso di errore, logga l'eccezione o gestiscila come preferisci
            System.out.println( "Errore nell'estrazione del nome utente dal token: " + ex.getMessage() );
            return null;
        }
    }

}

package com.adi.smartcalendar.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig( JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                           JwtAuthenticationFilter jwtAuthenticationFilter ) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    /* PASSWORD ENCODER
         * Questo metodo crea un oggetto PasswordEncoder.
         * Utilizza BCryptPasswordEncoder, che implementa l'algoritmo BCrypt per la codifica delle password.
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    /* AUTHENTICATION MANAGER
         * Questo metodo crea un oggetto AuthenticationManager.
         * Prende come input un oggetto AuthenticationConfiguration.
         * Utilizza la configurazione di autenticazione di Spring Security per ottenere l'AuthenticationManager.
         * L'AuthenticationManager è un componente chiave di Spring Security che gestisce il processo di autenticazione.
         * Restituisce l'oggetto AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager( AuthenticationConfiguration configuration ) throws Exception {

        return configuration.getAuthenticationManager();
    }


    /* SECURITY FILTER CHAIN
         * Questo metodo configura la sicurezza dell'applicazione.
         * Configura la politica CORS, disabilita CSRF, configura le regole di autorizzazione delle richieste, gestisce le eccezioni e configura la gestione della sessione.
         * Aggiunge il filtro per la gestione dell'autenticazione JWT.
     */
    @Bean
    SecurityFilterChain securityFilterChain( HttpSecurity http ) throws Exception {
        http
                .cors( (httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer
                        .configurationSource( ( httpServletRequest ) -> {
                            CorsConfiguration corsConfiguration = new CorsConfiguration();
                            corsConfiguration.setAllowedOrigins(
                                    List.of(
                                            "http://localhost:4200/",
                                            "http://127.0.0.1:4200/"
                                    ) );
                            corsConfiguration.setAllowedMethods(
                                    List.of(
                                            "GET",
                                            "POST",
                                            "PUT",
                                            "DELETE",
                                            "OPTIONS"
                                    ) );
                            corsConfiguration.setAllowedHeaders( List.of( "*" ) );
                            corsConfiguration.setExposedHeaders( List.of( "Authorization" ) );
                            corsConfiguration.setMaxAge( 3600L );
                            return corsConfiguration;
                        } )

                ) )
                .csrf( AbstractHttpConfigurer::disable)
                .authorizeHttpRequests( ( authorize ) -> authorize
                        // TUTTE LE RICHIESTE CHE INIZIANO CON /API/AUTH/ SONO ACCESSIBILI DA TUTTI
                        .requestMatchers( "/api/**" ).authenticated()

                        // TUTTE LE ALTRE RICHIESTE SONO ACCESSIBILI SOLO DA UTENTI AUTENTICATI
                        .anyRequest().authenticated() ).exceptionHandling( exception -> exception

                        // GESTIONE DELLE ECCEZIONI
                        .authenticationEntryPoint( jwtAuthenticationEntryPoint ) )

                // GESTIONE DELLA SESSIONE STATELESS
                .sessionManagement( session -> session
                        .sessionCreationPolicy( SessionCreationPolicy.STATELESS )
                );

        // AGGIUNGO IL FILTRO PER LA GESTIONE DELL'AUTENTICAZIONE JWT
        http.addFilterBefore( jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class );

        return http.build();
    }


}

package com.adi.smartcalendar.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    private final SecurityProperties securityProperties;

    public WebClientConfig(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Bean
    public WebClient webClient() {

        // Usa l'utente di lettura per l'autenticazione (modifica secondo le tue esigenze)
        String apiKeyWrite = securityProperties.getApikey().getWrite();
        String base = securityProperties.getApikey().getEndpoint().getBase();

        return WebClient.builder()
                .baseUrl(base)
                .defaultHeader("X-API-KEY", apiKeyWrite)
                .build();
    }
}


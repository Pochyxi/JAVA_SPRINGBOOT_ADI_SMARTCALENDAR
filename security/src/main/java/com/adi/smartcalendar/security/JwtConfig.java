package com.adi.smartcalendar.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app")
@Data
public class JwtConfig
{
	private String jwtSecret;
	private long jwtExpirationMilliseconds;
}

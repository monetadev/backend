package com.github.monetadev.backend.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Configuration
public class JwtConfig {
    @Value("${jwt.cookie.name:jwt}")
    private String cookieName;

    @Value("${jwt.cookie.maxAgeInSeconds:86400}")
    private int cookieMaxAge;

    @Value("${jwt.cookie.path:/}")
    private String cookiePath;

    @Value("${jwt.cookie.httpsOnly:false}")
    private Boolean secured;

    @Value("${jwt.cookie.same-site:Lax}")
    private String sameSitePolicy;

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public String cookieName() {
        return cookieName;
    }

    @Bean
    public Duration cookieMaxAge() {
        return Duration.ofSeconds(cookieMaxAge);
    }

    @Bean
    public String cookiePath() {
        return cookiePath;
    }

    @Bean
    public Boolean isSecured() {
        return secured;
    }

    @Bean
    public String sameSitePolicy() {
        return sameSitePolicy;
    }

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}

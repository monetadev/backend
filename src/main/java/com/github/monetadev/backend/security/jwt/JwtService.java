package com.github.monetadev.backend.security.jwt;

import com.github.monetadev.backend.model.Privilege;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private final Duration jwtExpiration;

    public JwtService(@Autowired SecretKey secretKey, @Autowired Duration jwtExpiration) {
        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtExpiration);

        Set<String> authorities = user.getRoles().stream()
                .flatMap(role -> role.getPrivileges().stream())
                .map(Privilege::getName)
                .collect(Collectors.toSet());

        user.getRoles().stream()
                .map(Role::getName)
                .forEach(authorities::add);

        return Jwts.builder()
                .issuer("Moneta")
                .subject(user.getUsername())
                .claim("userId", user.getId().toString())
                .claim("email", user.getEmail())
                .claim("authorities", authorities)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        String username = claims.getSubject();
        UUID userId = UUID.fromString(claims.get("userId", String.class));

        Object authoritiesObj = claims.get("authorities");
        List<SimpleGrantedAuthority> grantedAuthorities;

        if (authoritiesObj instanceof Collection<?>) {
            grantedAuthorities = ((Collection<?>) authoritiesObj).stream()
                    .filter(auth -> auth instanceof String)
                    .map(auth -> new SimpleGrantedAuthority((String) auth))
                    .toList();
        } else {
            grantedAuthorities = Collections.emptyList();
        }

        JwtUserDetails userDetails = new JwtUserDetails(userId, username, "", grantedAuthorities);

        return new UsernamePasswordAuthenticationToken(userDetails, token, grantedAuthorities);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean isValidToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException ignored) {
            return false;
        }
    }
}

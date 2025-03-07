package com.github.monetadev.backend.security.jwt;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

public class JwtUserDetails implements UserDetails {
    @Getter
    private final UUID userId;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUserDetails(UUID userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO: Implement
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO: Implement
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO: Implement
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO: Implement
        return true;
    }
}

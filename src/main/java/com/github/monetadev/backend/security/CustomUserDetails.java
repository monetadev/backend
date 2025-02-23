package com.github.monetadev.backend.security;

import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRoles() == null) {
            return Collections.emptyList();
        }

        return user.getRoles()
                .stream()
                .map(Role::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO: Implement account expiry.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO: Implement account locking.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO: Implement cred. expiry
        return true;
    }
}

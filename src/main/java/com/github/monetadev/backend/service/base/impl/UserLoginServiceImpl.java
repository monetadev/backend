package com.github.monetadev.backend.service.base.impl;

import com.github.monetadev.backend.graphql.type.pagination.PaginatedUserLogin;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.model.UserLogin;
import com.github.monetadev.backend.repository.UserLoginRepository;
import com.github.monetadev.backend.service.base.UserLoginService;
import com.github.monetadev.backend.service.security.AuthenticationService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@Transactional
public class UserLoginServiceImpl implements UserLoginService {

    private final AuthenticationService authenticationService;
    private final UserLoginRepository userLoginRepository;

    public UserLoginServiceImpl(AuthenticationService authenticationService, UserLoginRepository userLoginRepository) {
        this.authenticationService = authenticationService;
        this.userLoginRepository = userLoginRepository;
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public PaginatedUserLogin getCurrentAuthenticatedUserLogins(int page, int size) {
        User user = authenticationService.getAuthenticatedUser();
        Page<UserLogin> userLogins = userLoginRepository.findAllByUser(user, PageRequest.of(page, size));
        return PaginatedUserLogin.of()
                .items(userLogins.getContent())
                .totalPages(userLogins.getTotalPages())
                .totalElements(userLogins.getTotalElements())
                .currentPage(userLogins.getNumber())
                .build();
    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public PaginatedUserLogin getCurrentAuthenticatedUserLoginsBetweenDates(OffsetDateTime start, OffsetDateTime end, int page, int size) {
        User user = authenticationService.getAuthenticatedUser();
        Page<UserLogin> userLogins = userLoginRepository.findAllByUserAndLoginDateTimeBetween(user, start, end, PageRequest.of(page, size));
        return PaginatedUserLogin.of()
                .items(userLogins.getContent())
                .totalPages(userLogins.getTotalPages())
                .totalElements(userLogins.getTotalElements())
                .currentPage(userLogins.getNumber())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCurrentAuthenticatedUser24HourLoginStreak() {
        User user = authenticationService.getAuthenticatedUser();
        OffsetDateTime now = OffsetDateTime.now();

        Page<UserLogin> recentLogin = userLoginRepository.findAllByUserAndLoginDateTimeBetween(
                user, now.minusHours(24), now, PageRequest.of(0, 1));

        if (recentLogin.isEmpty()) {
            return 0;
        }

        int streak = 1;

        for (int i = 1; i <= 365; i++) {
            OffsetDateTime periodStart = now.minusHours(24 * (i + 1));
            OffsetDateTime periodEnd = now.minusHours(24 * i);
            Page<UserLogin> periodLogins = userLoginRepository.findAllByUserAndLoginDateTimeBetween(
                    user, periodStart, periodEnd, PageRequest.of(0, 1));

            if (periodLogins.isEmpty()) {
                break;
            }

            streak++;
        }

        return streak;
    }
}

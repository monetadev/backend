package com.github.monetadev.backend.service.base;

import com.github.monetadev.backend.graphql.type.pagination.PaginatedUserLogin;

import java.time.OffsetDateTime;

public interface UserLoginService {
    PaginatedUserLogin getCurrentAuthenticatedUserLogins(int page, int size);

    PaginatedUserLogin getCurrentAuthenticatedUserLoginsBetweenDates(OffsetDateTime start, OffsetDateTime end, int page, int size);

    Integer getCurrentAuthenticatedUser24HourLoginStreak();
}

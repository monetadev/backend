package com.github.monetadev.backend.graphql.controller;

import com.github.monetadev.backend.graphql.type.pagination.PaginatedUserLogin;
import com.github.monetadev.backend.service.base.UserLoginService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import java.time.OffsetDateTime;

@DgsComponent
public class UserLoginController {

    private final UserLoginService userLoginService;

    public UserLoginController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @DgsQuery
    public PaginatedUserLogin myLoginHistory(@InputArgument Integer page,
                                             @InputArgument Integer size) {
        return userLoginService.getCurrentAuthenticatedUserLogins(page, size);
    }

    @DgsQuery
    public PaginatedUserLogin myLoginHistoryBetweenDates(@InputArgument OffsetDateTime start,
                                                         @InputArgument OffsetDateTime end,
                                                         @InputArgument Integer page,
                                                         @InputArgument Integer size) {
        return userLoginService.getCurrentAuthenticatedUserLoginsBetweenDates(start, end, page, size);
    }

    @DgsQuery
    public Integer myLoginStreak() {
        return userLoginService.getCurrentAuthenticatedUser24HourLoginStreak();
    }
}

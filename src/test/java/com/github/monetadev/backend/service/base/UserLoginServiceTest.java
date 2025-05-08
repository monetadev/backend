package com.github.monetadev.backend.service.base;

import com.github.monetadev.backend.graphql.type.pagination.PaginatedUserLogin;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.model.UserLogin;
import com.github.monetadev.backend.repository.UserLoginRepository;
import com.github.monetadev.backend.service.base.impl.UserLoginServiceImpl;
import com.github.monetadev.backend.service.security.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserLoginServiceTest {
    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserLoginRepository userLoginRepository;

    @InjectMocks
    private UserLoginServiceImpl userLoginService;

    private User testUser;
    private List<UserLogin> userLogins;
    private Page<UserLogin> userLoginPage;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());

        UserLogin login1 = new UserLogin();
        login1.setId(UUID.randomUUID());
        login1.setUser(testUser);
        login1.setLoginDateTime(OffsetDateTime.now().minusHours(1));

        UserLogin login2 = new UserLogin();
        login2.setId(UUID.randomUUID());
        login2.setUser(testUser);
        login2.setLoginDateTime(OffsetDateTime.now().minusHours(3));

        userLogins = List.of(login1, login2);
        userLoginPage = new PageImpl<>(userLogins, PageRequest.of(0, 10), 2);

        when(authenticationService.getAuthenticatedUser()).thenReturn(testUser);
    }

    @Test
    void getCurrentAuthenticatedUserLogins_shouldReturnUserLogins() {
        when(userLoginRepository.findAllByUser(eq(testUser), any(PageRequest.class)))
                .thenReturn(userLoginPage);

        PaginatedUserLogin result = userLoginService.getCurrentAuthenticatedUserLogins(0, 10);

        assertEquals(userLogins, result.getItems());
        assertEquals(userLoginPage.getTotalPages(), result.getTotalPages());
        assertEquals(userLoginPage.getTotalElements(), result.getTotalElements());
        assertEquals(userLoginPage.getNumber(), result.getCurrentPage());

        verify(authenticationService, times(1)).getAuthenticatedUser();
        verify(userLoginRepository, times(1)).findAllByUser(eq(testUser), any(PageRequest.class));
    }

    @Test
    void getCurrentAuthenticatedUserLoginsBetweenDates_shouldReturnFilteredLogins() {
        OffsetDateTime start = OffsetDateTime.now().minusHours(5);
        OffsetDateTime end = OffsetDateTime.now();

        when(userLoginRepository.findAllByUserAndLoginDateTimeBetween(
                eq(testUser), eq(start), eq(end), any(PageRequest.class)))
                .thenReturn(userLoginPage);

        PaginatedUserLogin result = userLoginService.getCurrentAuthenticatedUserLoginsBetweenDates(
                start, end, 0, 10);

        assertEquals(userLogins, result.getItems());
        assertEquals(userLoginPage.getTotalPages(), result.getTotalPages());
        assertEquals(userLoginPage.getTotalElements(), result.getTotalElements());
        assertEquals(userLoginPage.getNumber(), result.getCurrentPage());

        verify(authenticationService, times(1)).getAuthenticatedUser();
        verify(userLoginRepository, times(1)).findAllByUserAndLoginDateTimeBetween(
                eq(testUser), eq(start), eq(end), any(PageRequest.class));
    }

    @Test
    void getCurrentAuthenticatedUser24HourLoginStreak_withNoLogins_shouldReturnZero() {
        when(userLoginRepository.findAllByUserAndLoginDateTimeBetween(
                eq(testUser), any(OffsetDateTime.class), any(OffsetDateTime.class), any(PageRequest.class)))
                .thenReturn(Page.empty());

        Integer result = userLoginService.getCurrentAuthenticatedUser24HourLoginStreak();

        assertEquals(0, result);
        verify(authenticationService, times(1)).getAuthenticatedUser();
    }

    @Test
    void getCurrentAuthenticatedUser24HourLoginStreak_withRecentLogins_shouldReturnStreak() {
        Page<UserLogin> recentLogin = new PageImpl<>(userLogins.subList(0, 1));
        when(userLoginRepository.findAllByUserAndLoginDateTimeBetween(
                eq(testUser), any(OffsetDateTime.class), any(OffsetDateTime.class), any(PageRequest.class)))
                .thenReturn(recentLogin)
                .thenReturn(recentLogin)
                .thenReturn(recentLogin)
                .thenReturn(Page.empty());

        Integer result = userLoginService.getCurrentAuthenticatedUser24HourLoginStreak();

        assertEquals(3, result);
        verify(authenticationService, times(1)).getAuthenticatedUser();
        // 1 for recent login + 3 for streak checks
        verify(userLoginRepository, times(4)).findAllByUserAndLoginDateTimeBetween(
                eq(testUser), any(OffsetDateTime.class), any(OffsetDateTime.class), any(PageRequest.class));
    }
}

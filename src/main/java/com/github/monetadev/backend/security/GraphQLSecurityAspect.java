package com.github.monetadev.backend.security;

import com.github.monetadev.backend.exception.PrivilegeAuthorizationException;
import com.github.monetadev.backend.model.Privilege;
import com.github.monetadev.backend.model.Role;
import com.github.monetadev.backend.model.User;
import com.github.monetadev.backend.service.security.AuthenticationService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GraphQLSecurityAspect {
    private final AuthenticationService authenticationService;

    public GraphQLSecurityAspect(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Around("@annotation(secureOperation)")
    public Object secureOperation(ProceedingJoinPoint joinPoint, SecureOperation secureOperation) throws Throwable {
        User currentUser = authenticationService.getAuthenticatedUser();

        if (hasAnyRole(currentUser, secureOperation.requiredRoles())) {
            return joinPoint.proceed();
        }

        if (hasAnyPrivilege(currentUser, secureOperation.requiredPrivileges())) {
            return joinPoint.proceed();
        }

        if (secureOperation.allowSelfAccess() && isSelfAccess(joinPoint, secureOperation.idField(), currentUser)) {
            return joinPoint.proceed();
        }

        throw new PrivilegeAuthorizationException("You do not have permission to access this resource.");
    }

    private boolean hasAnyRole(User user, String... roles) {
        if (roles.length == 0) return false;

        Set<String> userRoles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return Arrays.stream(roles)
                .anyMatch(userRoles::contains);
    }

    private boolean hasAnyPrivilege(User user, String... privileges) {
        if (privileges.length == 0) return false;

        Set<String> userPrivileges = user.getRoles().stream()
                .flatMap(role -> role.getPrivileges().stream())
                .map(Privilege::getName)
                .collect(Collectors.toSet());

        return Arrays.stream(privileges)
                .anyMatch(userPrivileges::contains);
    }

    private boolean isSelfAccess(ProceedingJoinPoint joinPoint, String idField, User currentUser) {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg == null) continue;

            try {
                if (arg instanceof UUID) {
                    return arg.equals(currentUser.getId());
                }

                Field field = ReflectionUtils.findField(arg.getClass(), idField);
                if (field != null) {
                    ReflectionUtils.makeAccessible(field);
                    Object idValue = field.get(arg);
                    if (idValue instanceof UUID) {
                        return idValue.equals(currentUser.getId());
                    }
                }
            } catch (IllegalAccessException ignored) {
                // TODO: Research exceptions in Aspect classes.
            }
        }
        return false;
    }
}

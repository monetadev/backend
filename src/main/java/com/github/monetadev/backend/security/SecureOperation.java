package com.github.monetadev.backend.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SecureOperation {
    String[] requiredRoles() default {};
    String[] requiredPrivileges() default {};
    boolean allowSelfAccess() default false;
    String idField() default "id";
}

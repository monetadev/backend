package com.github.monetadev.backend.exception;

public class InvalidAdminSecretException extends RuntimeException {
    public InvalidAdminSecretException(String message) {
        super(message);
    }
}

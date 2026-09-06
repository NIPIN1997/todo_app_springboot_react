package com.projectsbynipin.todo_app_backend.exception;

public class FailedToEncryptJwtRefreshTokenException extends RuntimeException {
    public FailedToEncryptJwtRefreshTokenException(String message) {
        super(message);
    }

    public FailedToEncryptJwtRefreshTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}

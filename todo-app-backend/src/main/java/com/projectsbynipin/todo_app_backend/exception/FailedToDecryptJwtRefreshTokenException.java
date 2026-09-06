package com.projectsbynipin.todo_app_backend.exception;

public class FailedToDecryptJwtRefreshTokenException extends RuntimeException {
    public FailedToDecryptJwtRefreshTokenException(String message) {
        super(message);
    }

    public FailedToDecryptJwtRefreshTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}

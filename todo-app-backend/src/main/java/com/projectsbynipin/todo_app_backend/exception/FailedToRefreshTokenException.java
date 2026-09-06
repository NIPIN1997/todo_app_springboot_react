package com.projectsbynipin.todo_app_backend.exception;

public class FailedToRefreshTokenException extends RuntimeException {
    public FailedToRefreshTokenException(String message) {
        super(message);
    }

    public FailedToRefreshTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}

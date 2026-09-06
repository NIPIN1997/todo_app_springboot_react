package com.projectsbynipin.todo_app_backend.exception;

public class FailedLoginRateLimitReachedException extends RuntimeException {
    public FailedLoginRateLimitReachedException(String message) {
        super(message);
    }

    public FailedLoginRateLimitReachedException(String message, Throwable cause) {
        super(message, cause);
    }
}

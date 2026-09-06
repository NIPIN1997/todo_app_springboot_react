package com.projectsbynipin.todo_app_backend.exception;

public class FailedToUnarchiveDashboardException extends RuntimeException {
    public FailedToUnarchiveDashboardException(String message) {
        super(message);
    }

    public FailedToUnarchiveDashboardException(String message, Throwable cause) {
        super(message, cause);
    }
}

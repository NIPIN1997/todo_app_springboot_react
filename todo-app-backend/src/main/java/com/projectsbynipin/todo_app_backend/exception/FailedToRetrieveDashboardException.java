package com.projectsbynipin.todo_app_backend.exception;

public class FailedToRetrieveDashboardException extends RuntimeException {
    public FailedToRetrieveDashboardException(String message) {
        super(message);
    }

    public FailedToRetrieveDashboardException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.projectsbynipin.todo_app_backend.exception;

public class FailedToDeleteDashboardException extends RuntimeException {
    public FailedToDeleteDashboardException(String message) {
        super(message);
    }

    public FailedToDeleteDashboardException(String message, Throwable cause) {
        super(message, cause);
    }
}

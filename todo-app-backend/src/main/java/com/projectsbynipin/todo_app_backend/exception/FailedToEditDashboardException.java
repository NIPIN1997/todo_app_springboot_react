package com.projectsbynipin.todo_app_backend.exception;

public class FailedToEditDashboardException extends RuntimeException {
    public FailedToEditDashboardException(String message) {
        super(message);
    }

    public FailedToEditDashboardException(String message, Throwable cause) {
        super(message, cause);
    }
}

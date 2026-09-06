package com.projectsbynipin.todo_app_backend.exception;

public class FailedToCreateDashboardException extends RuntimeException {
    public FailedToCreateDashboardException(String message) {
        super(message);
    }
    public FailedToCreateDashboardException(String message, Throwable cause) {
        super(message, cause);
    }
}

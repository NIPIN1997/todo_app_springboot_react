package com.projectsbynipin.todo_app_backend.exception;

public class FailedToArchiveDashboardException extends RuntimeException {
    public FailedToArchiveDashboardException(String message) {
        super(message);
    }

    public FailedToArchiveDashboardException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.projectsbynipin.todo_app_backend.exception;

public class FailedToCreateDashboardColumnException extends RuntimeException {
    public FailedToCreateDashboardColumnException(String message) {
        super(message);
    }

    public FailedToCreateDashboardColumnException(String message, Throwable cause) {
        super(message, cause);
    }
}

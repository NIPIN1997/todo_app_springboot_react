package com.projectsbynipin.todo_app_backend.exception;

public class FailedToEditDashboardColumnNameException extends RuntimeException {
    public FailedToEditDashboardColumnNameException(String message) {
        super(message);
    }

    public FailedToEditDashboardColumnNameException(String message, Throwable cause) {
        super(message, cause);
    }
}

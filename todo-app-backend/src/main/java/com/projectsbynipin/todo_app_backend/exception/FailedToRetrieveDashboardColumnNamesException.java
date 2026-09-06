package com.projectsbynipin.todo_app_backend.exception;

public class FailedToRetrieveDashboardColumnNamesException extends RuntimeException {
    public FailedToRetrieveDashboardColumnNamesException(String message) {
        super(message);
    }

    public FailedToRetrieveDashboardColumnNamesException(String message, Throwable cause) {
        super(message, cause);
    }
}

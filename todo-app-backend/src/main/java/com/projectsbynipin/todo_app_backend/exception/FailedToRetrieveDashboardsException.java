package com.projectsbynipin.todo_app_backend.exception;

public class FailedToRetrieveDashboardsException extends RuntimeException {
    public FailedToRetrieveDashboardsException(String message) {
        super(message);
    }

    public FailedToRetrieveDashboardsException(String message, Throwable cause) {
        super(message, cause);
    }
}

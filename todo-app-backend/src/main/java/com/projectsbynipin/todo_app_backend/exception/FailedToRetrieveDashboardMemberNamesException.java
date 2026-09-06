package com.projectsbynipin.todo_app_backend.exception;

public class FailedToRetrieveDashboardMemberNamesException extends RuntimeException {
    public FailedToRetrieveDashboardMemberNamesException(String message) {
        super(message);
    }

    public FailedToRetrieveDashboardMemberNamesException(String message, Throwable cause) {
        super(message, cause);
    }
}

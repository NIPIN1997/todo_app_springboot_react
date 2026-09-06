package com.projectsbynipin.todo_app_backend.exception;

public class FailedToRemoveDashboardMemberException extends RuntimeException {
    public FailedToRemoveDashboardMemberException(String message) {
        super(message);
    }

    public FailedToRemoveDashboardMemberException(String message, Throwable cause) {
        super(message, cause);
    }
}

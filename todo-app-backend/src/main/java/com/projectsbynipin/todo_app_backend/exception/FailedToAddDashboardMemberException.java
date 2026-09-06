package com.projectsbynipin.todo_app_backend.exception;

public class FailedToAddDashboardMemberException extends RuntimeException {
    public FailedToAddDashboardMemberException(String message) {
        super(message);
    }

    public FailedToAddDashboardMemberException(String message, Throwable cause) {
        super(message, cause);
    }
}

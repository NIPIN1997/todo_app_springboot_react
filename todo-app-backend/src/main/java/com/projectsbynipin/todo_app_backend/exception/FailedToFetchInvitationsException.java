package com.projectsbynipin.todo_app_backend.exception;

public class FailedToFetchInvitationsException extends RuntimeException {
    public FailedToFetchInvitationsException(String message) {
        super(message);
    }

    public FailedToFetchInvitationsException(String message, Throwable cause) {
        super(message, cause);
    }
}

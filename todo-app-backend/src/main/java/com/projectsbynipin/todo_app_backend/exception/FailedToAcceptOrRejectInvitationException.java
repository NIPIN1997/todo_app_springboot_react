package com.projectsbynipin.todo_app_backend.exception;

public class FailedToAcceptOrRejectInvitationException extends RuntimeException {
    public FailedToAcceptOrRejectInvitationException(String message) {
        super(message);
    }

    public FailedToAcceptOrRejectInvitationException(String message, Throwable cause) {
        super(message, cause);
    }
}

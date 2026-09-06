package com.projectsbynipin.todo_app_backend.exception;

public class FailedToCreateInvitationException extends RuntimeException {
    public FailedToCreateInvitationException(String message) {
        super(message);
    }

    public FailedToCreateInvitationException(String message, Throwable cause) {
        super(message, cause);
    }
}

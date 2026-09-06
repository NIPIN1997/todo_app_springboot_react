package com.projectsbynipin.todo_app_backend.exception;

public class FailedToCheckUsernameException extends RuntimeException {
    public FailedToCheckUsernameException(String message) {
        super(message);
    }

    public FailedToCheckUsernameException(String message, Throwable cause) {
        super(message, cause);
    }
}

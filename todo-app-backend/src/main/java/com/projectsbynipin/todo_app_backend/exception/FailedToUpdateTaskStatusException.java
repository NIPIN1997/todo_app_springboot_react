package com.projectsbynipin.todo_app_backend.exception;

public class FailedToUpdateTaskStatusException extends RuntimeException {
    public FailedToUpdateTaskStatusException(String message) {
        super(message);
    }

    public FailedToUpdateTaskStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}

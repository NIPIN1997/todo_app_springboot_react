package com.projectsbynipin.todo_app_backend.exception;

public class FailedToDeleteTaskException extends RuntimeException {
    public FailedToDeleteTaskException(String message) {
        super(message);
    }

    public FailedToDeleteTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}

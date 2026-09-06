package com.projectsbynipin.todo_app_backend.exception;

public class FailedToAddTaskException extends RuntimeException {
    public FailedToAddTaskException(String message) {
        super(message);
    }

    public FailedToAddTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}

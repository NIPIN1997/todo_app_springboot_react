package com.projectsbynipin.todo_app_backend.exception;

public class FailedToFetchTaskException extends RuntimeException {
    public FailedToFetchTaskException(String message) {
        super(message);
    }

    public FailedToFetchTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}

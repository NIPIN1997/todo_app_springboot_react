package com.projectsbynipin.todo_app_backend.exception;

public class FailedToEditTaskException extends RuntimeException {
    public FailedToEditTaskException(String message) {
        super(message);
    }

    public FailedToEditTaskException(String message, Throwable cause) {super(message, cause);}
}

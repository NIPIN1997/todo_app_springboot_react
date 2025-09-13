package com.projectsbynipin.todo_app_backend.exception;

public class FailedToSaveUserException extends RuntimeException {
    public FailedToSaveUserException(String message) {
        super(message);
    }
}

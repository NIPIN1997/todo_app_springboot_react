package com.projectsbynipin.todo_app_backend.exception;

public class FailedToEditUserException extends RuntimeException {
    public FailedToEditUserException(String message) {
        super(message);
    }
}

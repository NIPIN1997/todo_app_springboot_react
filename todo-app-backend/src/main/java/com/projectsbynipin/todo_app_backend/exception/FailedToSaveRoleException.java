package com.projectsbynipin.todo_app_backend.exception;

public class FailedToSaveRoleException extends RuntimeException {
    public FailedToSaveRoleException(String message) {
        super(message);
    }
}

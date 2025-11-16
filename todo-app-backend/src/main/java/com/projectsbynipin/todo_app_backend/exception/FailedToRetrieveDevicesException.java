package com.projectsbynipin.todo_app_backend.exception;

public class FailedToRetrieveDevicesException extends RuntimeException {
    public FailedToRetrieveDevicesException(String message) {
        super(message);
    }
}

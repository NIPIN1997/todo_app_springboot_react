package com.projectsbynipin.todo_app_backend.exception;

public class FailedToLogoutDeviceException extends RuntimeException {
    public FailedToLogoutDeviceException(String message) {
        super(message);
    }
}

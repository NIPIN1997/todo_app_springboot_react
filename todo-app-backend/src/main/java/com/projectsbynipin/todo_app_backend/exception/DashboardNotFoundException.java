package com.projectsbynipin.todo_app_backend.exception;

public class DashboardNotFoundException extends RuntimeException {
    public DashboardNotFoundException(String message) {
        super(message);
    }

    public DashboardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

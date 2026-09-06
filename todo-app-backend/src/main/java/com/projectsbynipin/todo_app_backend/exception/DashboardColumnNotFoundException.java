package com.projectsbynipin.todo_app_backend.exception;

public class DashboardColumnNotFoundException extends RuntimeException {
    public DashboardColumnNotFoundException(String message) {
        super(message);
    }

    public DashboardColumnNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

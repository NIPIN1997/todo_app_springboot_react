package com.projectsbynipin.todo_app_backend.exception;

public class FailedToPromoteMemberException extends RuntimeException {
    public FailedToPromoteMemberException(String message) {
        super(message);
    }

    public FailedToPromoteMemberException(String message, Throwable cause) {
        super(message, cause);
    }
}

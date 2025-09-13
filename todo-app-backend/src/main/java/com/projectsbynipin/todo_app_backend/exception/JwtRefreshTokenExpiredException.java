package com.projectsbynipin.todo_app_backend.exception;

public class JwtRefreshTokenExpiredException extends RuntimeException {
    public JwtRefreshTokenExpiredException(String message) {
        super(message);
    }
}

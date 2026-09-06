package com.projectsbynipin.todo_app_backend.dto.loggingdtos;

import java.time.LocalDateTime;

public record ErrorLogEvent(
        String username,
        String exceptionType,
        String message,
        String stackTrace,
        LocalDateTime timeStamp
) {
}

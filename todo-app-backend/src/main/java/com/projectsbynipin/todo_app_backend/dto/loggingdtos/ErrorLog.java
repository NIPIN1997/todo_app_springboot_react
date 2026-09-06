package com.projectsbynipin.todo_app_backend.dto.loggingdtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorLog {
    private String exceptionType;
    private String message;
    private String stackTrace;
    private LocalDateTime time;
}

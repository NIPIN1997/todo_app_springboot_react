package com.projectsbynipin.todo_app_backend.utility;


import com.projectsbynipin.todo_app_backend.dto.ApiResponse;
import com.projectsbynipin.todo_app_backend.enums.Status;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ApiResponseCreator {

    private ApiResponseCreator() {
    }

    public static ApiResponse<Void> success(String message, HttpStatus httpStatus) {
        return ApiResponse.<Void>builder()
                .status(Status.SUCCESS)
                .message(message)
                .httpStatus(httpStatus)
                .time(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data, HttpStatus httpStatus) {
        return ApiResponse.<T>builder()
                .status(Status.SUCCESS)
                .message(message)
                .data(data)
                .httpStatus(httpStatus)
                .time(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<Void> error(String message, HttpStatus httpStatus) {
        return ApiResponse.<Void>builder()
                .status(Status.ERROR)
                .message(message)
                .httpStatus(httpStatus)
                .time(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, T data, HttpStatus httpStatus) {
        return ApiResponse.<T>builder()
                .status(Status.ERROR)
                .message(message)
                .data(data)
                .httpStatus(httpStatus)
                .time(LocalDateTime.now())
                .build();
    }
}

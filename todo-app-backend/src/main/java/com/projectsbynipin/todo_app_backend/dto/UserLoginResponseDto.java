package com.projectsbynipin.todo_app_backend.dto;

public record UserLoginResponseDto(
        String jwtToken,
        boolean rememberMeEnabled
) {
}

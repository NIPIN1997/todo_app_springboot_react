package com.projectsbynipin.todo_app_backend.dto;

public record LoginResponseDto(
        String jwtToken,
        String jwtRefreshToken
) {
}

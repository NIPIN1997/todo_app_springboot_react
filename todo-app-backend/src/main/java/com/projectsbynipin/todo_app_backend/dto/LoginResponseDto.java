package com.projectsbynipin.todo_app_backend.dto;

public record LoginResponseDto(
        ApiResponse<Token> apiResponse, String refreshToken
) {
    public record Token(String jwtToken) {
    }
}

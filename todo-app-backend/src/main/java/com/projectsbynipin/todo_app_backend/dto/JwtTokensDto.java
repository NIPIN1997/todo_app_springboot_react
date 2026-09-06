package com.projectsbynipin.todo_app_backend.dto;

public record JwtTokensDto(
        String jwtToken,
        String refreshToken,
        String rememberMeToken,
        String deviceId
) {
}

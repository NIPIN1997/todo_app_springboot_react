package com.projectsbynipin.todo_app_backend.dto;

import jakarta.validation.constraints.NotNull;

public record RememberMeLoginRequestDto(
        @NotNull(message = "Device ID is required") String deviceId,
        @NotNull(message = "Remember me token is required") String rememberMeToken
) {
}

package com.projectsbynipin.todo_app_backend.dto;

public record RememberMeLoginRequestDto(
        String deviceId,
        String rememberMeToken
) {
}

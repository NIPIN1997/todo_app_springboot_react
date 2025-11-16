package com.projectsbynipin.todo_app_backend.dto;

import java.util.UUID;

public record LoggedInDevicesResponseDto(
        String browser,
        String os,
        String osVersion,
        UUID deviceId
) {
}

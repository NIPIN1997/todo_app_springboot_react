package com.projectsbynipin.todo_app_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddDashboardMemberRequestDto(
        @NotNull(message = "Dashboard ID cannot be null.")
        UUID dashboardId,
        @NotBlank(message = "Username cannot be blank.")
        String username
) {
}

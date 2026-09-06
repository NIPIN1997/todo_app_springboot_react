package com.projectsbynipin.todo_app_backend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RemoveDashboardMemberRequestDto(
        @NotNull(message = "Dashboard ID cannot be null.")
        UUID dashboardId,
        @NotNull(message = "Member ID cannot be null.")
        UUID memberId
) {
}

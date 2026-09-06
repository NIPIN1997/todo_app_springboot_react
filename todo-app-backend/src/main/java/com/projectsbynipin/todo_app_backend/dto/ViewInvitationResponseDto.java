package com.projectsbynipin.todo_app_backend.dto;

import java.util.UUID;

public record ViewInvitationResponseDto(UUID id, String dashboardName, String masterName) {
}

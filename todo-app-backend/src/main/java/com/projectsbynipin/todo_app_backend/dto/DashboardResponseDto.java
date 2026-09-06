package com.projectsbynipin.todo_app_backend.dto;

import java.util.UUID;

public record DashboardResponseDto(UUID id, String name, UUID masterId, String masterName, boolean isPrivate
) {
}

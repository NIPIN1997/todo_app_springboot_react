package com.projectsbynipin.todo_app_backend.dto;

import java.util.Map;
import java.util.UUID;

public record DetailedDashboardResponseDto(UUID id, String name, boolean isMaster, boolean isPrivate,
                                           long numberOfMembers, String masterName, boolean isArchived,
                                           Map<String, Long> statusMap) {
}

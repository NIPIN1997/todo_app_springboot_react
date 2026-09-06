package com.projectsbynipin.todo_app_backend.dto;

import java.util.List;
import java.util.UUID;

public record EditDashboardViewDto(
        UUID id,
        String name,
        boolean isPrivate,
        List<EditDashboardMember> members,
        List<EditDashboardColumn> columns
) {
    public record EditDashboardMember(
            UUID id,
            String name
    ) {
    }

    public record EditDashboardColumn(
            UUID id,
            String name,
            long position
    ) {
    }
}

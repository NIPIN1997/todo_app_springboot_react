package com.projectsbynipin.todo_app_backend.dto;

import java.time.LocalDate;
import java.util.UUID;

public record EditTaskViewDto(
        UUID id,
        String title,
        String description,
        LocalDate dueDate,
        Boolean isPrivate,
        EditTaskViewColumn editTaskViewColumn,
        EditTaskViewUser editTaskViewUser
) {
    public record EditTaskViewColumn(
            UUID id,
            String name
    ) {
    }

    public record EditTaskViewUser(
            UUID id,
            String name
    ) {
    }
}

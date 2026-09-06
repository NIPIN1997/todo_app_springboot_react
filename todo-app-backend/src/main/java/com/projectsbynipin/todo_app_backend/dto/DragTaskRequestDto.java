package com.projectsbynipin.todo_app_backend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DragTaskRequestDto(
        @NotNull(message = "Task ID cannot be null")
        UUID taskId,
        @NotNull(message = "Column ID cannot be null")
        UUID columnId
) {
}

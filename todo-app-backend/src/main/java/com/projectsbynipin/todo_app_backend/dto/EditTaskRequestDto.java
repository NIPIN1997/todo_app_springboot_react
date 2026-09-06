package com.projectsbynipin.todo_app_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record EditTaskRequestDto(
        @NotNull(message = "Task ID cannot be blank")
        UUID id,
        @NotBlank(message = "Title cannot be blank.")
        @Size(min = 3, message = "Title should contain minimum 3 characters.")
        String title,
        String description,
        @NotNull(message = "Due date cannot be blank.")
        LocalDate dueDate,
        @NotNull(message = "Column ID cannot be blank.")
        UUID column,
        UUID assignedTo
) {
}

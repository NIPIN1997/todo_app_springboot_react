package com.projectsbynipin.todo_app_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

public record ViewTaskResponseDto(
        UUID id,
        String title,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate dueDate,
        String assignedTo,
        String status,
        String description,
        long progress,
        boolean isPrivateDashboard
) {
}

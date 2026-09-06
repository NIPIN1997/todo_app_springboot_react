package com.projectsbynipin.todo_app_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record EditDashboardColumnNameRequestDto(
        @NotNull(message = "Column ID cannot be null")
        UUID columnID,
        @NotBlank(message = "Column name cannot be blank")
        @Size(min = 3, max = 100, message = "Column name should contain 3 to 100 characters")
        String columnName
) {
}

package com.projectsbynipin.todo_app_backend.dto;

import java.util.UUID;

public record ColumnNameIdResponseDto(
        UUID id,
        String name
) {
}

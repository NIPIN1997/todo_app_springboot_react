package com.projectsbynipin.todo_app_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record AddRoleRequestDto(
        @NotBlank(message = "Role name cannot be blank.")
        String name
) {
}

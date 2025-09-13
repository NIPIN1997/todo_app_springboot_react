package com.projectsbynipin.todo_app_backend.dto;

public record ViewUserResponseDto(
        String name,
        String email,
        String contact
) {
}

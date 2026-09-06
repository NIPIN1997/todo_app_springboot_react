package com.projectsbynipin.todo_app_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AddDashboardRequestDto(
        @NotBlank(message = "Dashboard name cannot be blank.")
        @Size(min = 3, max = 100, message = "Dashboard name should be between 3 to 100 characters.")
        String name,
        List<AddFieldRequestDto> fields,
        List<String> members,
        @NotNull
        boolean isPrivate) {
    public record AddFieldRequestDto(String fieldName, String fieldPosition) {
    }
}

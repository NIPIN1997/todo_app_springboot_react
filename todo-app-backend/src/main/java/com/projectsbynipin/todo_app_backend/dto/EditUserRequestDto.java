package com.projectsbynipin.todo_app_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EditUserRequestDto(
        @NotBlank(message = "Name cannot be blank.")
        @Size(min = 3, message = "Name should consist of minimum 3 letters.")
        @Pattern(regexp = "^[A-Za-z ]+$", message = "Name should consist of only alphabets and spaces.")
        String name,
        String contact
) {
}

package com.projectsbynipin.todo_app_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(
        @NotBlank(message = "Email cannot be blank.")
        @Email(message = "Invalid email format.")
        String email,
        @NotBlank(message = "Password cannot be blank.")
        @Size(min = 5, message = "Password should contain minimum 5 characters.")
        String password
) {
}

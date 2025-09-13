package com.projectsbynipin.todo_app_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddUserRequestDto(
        @NotBlank(message = "Name cannot be blank.")
        @Size(min = 3, message = "Name should consist of minimum 3 letters.")
        @Pattern(regexp = "^[A-Za-z ]+$", message = "Name should consist of only alphabets and spaces.")
        String name,
        @NotBlank(message = "Email cannot be blank.")
        @Email(message = "Invalid email format.")
        String email,
        String contact,
        @NotBlank(message = "Password cannot be blank.")
        @Size(min = 5, message = "Password should contain minimum 5 characters.")
        String password
) {
}

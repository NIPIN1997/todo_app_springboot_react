package com.projectsbynipin.todo_app_backend.service;

import com.projectsbynipin.todo_app_backend.dto.AddRoleRequestDto;
import com.projectsbynipin.todo_app_backend.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

public interface RoleService {
    ResponseEntity<ApiResponse<Void>> addRole(@Valid AddRoleRequestDto addRoleRequestDto);
}

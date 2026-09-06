package com.projectsbynipin.todo_app_backend.controller;

import com.projectsbynipin.todo_app_backend.dto.AddRoleRequestDto;
import com.projectsbynipin.todo_app_backend.dto.ApiResponse;
import com.projectsbynipin.todo_app_backend.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Roles",
        description = "APIs related to roles"
)
@RestController
@RequestMapping(path = "/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(
            summary = "Create new role",
            description = "Create new role in the system",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(path = "/create-role")
    public ResponseEntity<ApiResponse<Void>> createRole(@Valid @RequestBody AddRoleRequestDto addRoleRequestDto) {
        ApiResponse<Void> apiResponse = roleService.createRole(addRoleRequestDto);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
}

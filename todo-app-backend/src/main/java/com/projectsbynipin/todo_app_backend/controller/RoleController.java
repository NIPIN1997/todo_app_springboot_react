package com.projectsbynipin.todo_app_backend.controller;

import com.projectsbynipin.todo_app_backend.dto.AddRoleRequestDto;
import com.projectsbynipin.todo_app_backend.dto.ApiResponse;
import com.projectsbynipin.todo_app_backend.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping(path = "/add-role")
    public ResponseEntity<ApiResponse<Void>> addRole(@Valid @RequestBody AddRoleRequestDto addRoleRequestDto) {
        return roleService.addRole(addRoleRequestDto);
    }
}

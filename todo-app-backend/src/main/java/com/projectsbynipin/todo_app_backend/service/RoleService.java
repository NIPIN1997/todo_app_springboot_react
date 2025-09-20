package com.projectsbynipin.todo_app_backend.service;

import com.projectsbynipin.todo_app_backend.dto.AddRoleRequestDto;
import com.projectsbynipin.todo_app_backend.dto.ApiResponse;

public interface RoleService {

    ApiResponse<Void> createRole(AddRoleRequestDto addRoleRequestDto);
}

package com.projectsbynipin.todo_app_backend.service.impl;

import com.projectsbynipin.todo_app_backend.dto.AddRoleRequestDto;
import com.projectsbynipin.todo_app_backend.dto.ApiResponse;
import com.projectsbynipin.todo_app_backend.entity.Role;
import com.projectsbynipin.todo_app_backend.exception.FailedToSaveRoleException;
import com.projectsbynipin.todo_app_backend.exception.RoleAlreadyExistsException;
import com.projectsbynipin.todo_app_backend.repository.RoleRepository;
import com.projectsbynipin.todo_app_backend.service.RoleService;
import com.projectsbynipin.todo_app_backend.utility.ApiResponseCreator;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public ApiResponse<Void> createRole(AddRoleRequestDto addRoleRequestDto) {
        try {
            Role role = roleRepository.findByName(addRoleRequestDto.name());
            if (role != null) {
                throw new RoleAlreadyExistsException(Constants.Role.ROLE_ALREADY_EXISTS);
            }

            roleRepository.save(
                    Role.builder()
                            .name(addRoleRequestDto.name())
                            .build()
            );
            return ApiResponseCreator.success(Constants.Role.ROLE_CREATED, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new FailedToSaveRoleException(Constants.Role.FAILED_TO_CREATE_ROLE, e);
        }
    }
}

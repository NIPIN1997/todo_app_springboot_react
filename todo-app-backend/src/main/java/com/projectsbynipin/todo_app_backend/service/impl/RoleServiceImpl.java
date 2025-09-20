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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public ApiResponse<Void> createRole(AddRoleRequestDto addRoleRequestDto) {
        Role role = roleRepository.findByName(addRoleRequestDto.name());
        if (role != null) {
            logger.error("Failed to add role with name : {} because role already exists.", addRoleRequestDto.name());
            throw new RoleAlreadyExistsException(Constants.Role.ROLE_ALREADY_EXISTS);
        }
        try {
            Role role1 = roleRepository.save(
                    Role.builder()
                            .name(addRoleRequestDto.name())
                            .build()
            );
            logger.info("New role added with ID : {} and name : {}.", role1.getId(), role1.getName());
            return ApiResponseCreator.success(Constants.Role.ROLE_CREATED, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to add role with name : {}", addRoleRequestDto.name(), e);
            throw new FailedToSaveRoleException(Constants.Role.FAILED_TO_CREATE_ROLE);
        }
    }
}

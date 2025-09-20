package com.projectsbynipin.todo_app_backend.service;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;

import java.util.UUID;

public interface UserService {
    ApiResponse<Void> createAdmin(AddUserRequestDto addUserRequestDto);

    ApiResponse<Void> createUser(AddUserRequestDto addUserRequestDto);

    LoginResponseDto login(LoginRequestDto loginRequestDto);

    ApiResponse<ViewUserResponseDto> getUser(UUID userId, UserInfoDetails userInfoDetails);
}

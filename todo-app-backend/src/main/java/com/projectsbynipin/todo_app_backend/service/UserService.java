package com.projectsbynipin.todo_app_backend.service;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface UserService {
    ResponseEntity<ApiResponse<Void>> addAdmin(AddUserRequestDto addUserRequestDto);

    ResponseEntity<ApiResponse<Void>> addUser(@Valid AddUserRequestDto addUserRequestDto);

    ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid LoginRequestDto loginRequestDto);

    ResponseEntity<ApiResponse<ViewUserResponseDto>> getUser(UUID userId, UserInfoDetails userInfoDetails);
}

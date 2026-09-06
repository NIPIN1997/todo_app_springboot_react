package com.projectsbynipin.todo_app_backend.service;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface UserService {
    ApiResponse<Void> createAdmin(AddUserRequestDto addUserRequestDto);

    ApiResponse<Void> createUser(AddUserRequestDto addUserRequestDto);

    JwtTokensDto login(LoginRequestDto loginRequestDto, HttpServletRequest httpServletRequest);

    ApiResponse<ViewUserResponseDto> getUser(UserInfoDetails userInfoDetails);

    JwtTokensDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto, HttpServletRequest httpServletRequest);

    ApiResponse<Void> editUser(EditUserRequestDto editUserRequestDto, UserInfoDetails userInfoDetails);

    ApiResponse<Void> logout(String username, LogoutRequestDto logoutRequestDto, HttpServletRequest httpServletRequest);

    ApiResponse<List<LoggedInDevicesResponseDto>> loggedInDevices(String username);

    ApiResponse<Void> logoutDevices(UUID deviceId, UserInfoDetails userInfoDetails);

    JwtTokensDto rememberMeLogin(@Valid RememberMeLoginRequestDto rememberMeLoginRequestDto, HttpServletRequest httpServletRequest);

    ApiResponse<String> checkUsernameExistence(String username, UserInfoDetails userInfoDetails);
}

package com.projectsbynipin.todo_app_backend.controller;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.service.UserService;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {

    private final UserService userService;

    @Value("${spring.https}")
    private boolean isHttps;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/create-admin")
    public ResponseEntity<ApiResponse<Void>> createAdmin(@Valid @RequestBody AddUserRequestDto addUserRequestDto) {
        ApiResponse<Void> apiResponse = userService.createAdmin(addUserRequestDto);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping(path = "/signup")
    public ResponseEntity<ApiResponse<Void>> createUser(@Valid @RequestBody AddUserRequestDto addUserRequestDto) {
        ApiResponse<Void> apiResponse = userService.createUser(addUserRequestDto);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        ApiResponse<LoginResponseDto> apiResponse = userService.login(loginRequestDto);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/get-user/{userId}")
    public ResponseEntity<ApiResponse<ViewUserResponseDto>> getUser(@PathVariable UUID userId, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<ViewUserResponseDto> apiResponse = userService.getUser(userId, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping(path = "/edit-user/{userId}")
    public ResponseEntity<ApiResponse<Void>> editUser(@PathVariable UUID userId, @RequestBody EditUserRequestDto editUserRequestDto) {
        ApiResponse<Void> apiResponse = userService.editUser(userId, editUserRequestDto);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping(path = "/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponseDto>> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        ApiResponse<LoginResponseDto> apiResponse = userService.refreshToken(refreshTokenRequestDto.refreshToken());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = userService.logout(userInfoDetails.getUsername());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
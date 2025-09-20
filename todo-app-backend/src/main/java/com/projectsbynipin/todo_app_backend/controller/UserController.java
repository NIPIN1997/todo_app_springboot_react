package com.projectsbynipin.todo_app_backend.controller;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.service.UserService;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
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
    public ResponseEntity<ApiResponse<LoginResponseDto.Token>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = userService.login(loginRequestDto);
        ResponseCookie responseCookie = ResponseCookie.from("refresh-token", loginResponseDto.refreshToken())
                .httpOnly(true)
                .secure(isHttps)
                .path("/api/v1/users/refresh-token")
                .sameSite("Strict")
                .maxAge(Duration.ofHours(1))
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(loginResponseDto.apiResponse());
    }

    @GetMapping(path = "/get-user/{userId}")
    public ResponseEntity<ApiResponse<ViewUserResponseDto>> getUser(@PathVariable UUID userId, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<ViewUserResponseDto> apiResponse = userService.getUser(userId, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
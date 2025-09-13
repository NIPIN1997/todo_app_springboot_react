package com.projectsbynipin.todo_app_backend.controller;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.service.UserService;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping(path = "/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/add-admin")
    public ResponseEntity<ApiResponse<Void>> addAdmin(@Valid @RequestBody AddUserRequestDto addUserRequestDto) {
        return userService.addAdmin(addUserRequestDto);
    }

    @PostMapping(path = "/signup")
    public ResponseEntity<ApiResponse<Void>> addUser(@Valid @RequestBody AddUserRequestDto addUserRequestDto) {
        return userService.addUser(addUserRequestDto);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return userService.login(loginRequestDto);
    }

    @GetMapping(path = "/get-user/{userId}")
    public ResponseEntity<ApiResponse<ViewUserResponseDto>> getUser(@PathVariable UUID userId, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        return userService.getUser(userId, userInfoDetails);
    }
}
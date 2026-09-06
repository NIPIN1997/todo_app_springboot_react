package com.projectsbynipin.todo_app_backend.controller;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.service.UserService;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import com.projectsbynipin.todo_app_backend.utility.LoginResponseCreator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Users",
        description = "APIs related to users"
)
@RestController
@RequestMapping(path = "/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Value("${spring.https}")
    private boolean isHttps;

    @Operation(
            summary = "Create an admin",
            description = "Create a new admin in the system"
    )
    @PostMapping(path = "/create-admin")
    public ResponseEntity<ApiResponse<Void>> createAdmin(@Valid @RequestBody AddUserRequestDto addUserRequestDto) {
        ApiResponse<Void> apiResponse = userService.createAdmin(addUserRequestDto);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Signup",
            description = "Create a new user in the system"
    )
    @PostMapping(path = "/signup")
    public ResponseEntity<ApiResponse<Void>> createUser(@Valid @RequestBody AddUserRequestDto addUserRequestDto) {
        ApiResponse<Void> apiResponse = userService.createUser(addUserRequestDto);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Login",
            description = "Login API for user"
    )
    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse<UserLoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletRequest httpServletRequest) {
        JwtTokensDto jwtTokensDto = userService.login(loginRequestDto, httpServletRequest);
        return LoginResponseCreator.createLoginResponse(jwtTokensDto);
    }

    @Operation(
            summary = "Fetch user",
            description = "Fetch the details of a particular user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/get-user")
    public ResponseEntity<ApiResponse<ViewUserResponseDto>> getUser(@AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<ViewUserResponseDto> apiResponse = userService.getUser(userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Edit user",
            description = "Edit the details of an user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/edit-user")
    public ResponseEntity<ApiResponse<Void>> editUser(@RequestBody EditUserRequestDto editUserRequestDto, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = userService.editUser(editUserRequestDto, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Refresh JWT token",
            description = "API to refresh the JWT token",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(path = "/refresh-token")
    public ResponseEntity<ApiResponse<UserLoginResponseDto>> refreshToken(@CookieValue(value = "refreshToken", required = true) String refreshToken, @CookieValue(value = "deviceId", required = true) String deviceId, HttpServletRequest httpServletRequest) {
        JwtTokensDto jwtTokensDto = userService.refreshToken(new RefreshTokenRequestDto(refreshToken, deviceId), httpServletRequest);
        return LoginResponseCreator.createLoginResponse(jwtTokensDto);
    }

    @Operation(
            summary = "Logout",
            description = "Logout API for the user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(path = "/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@CookieValue(value = "deviceId", required = true) String deviceId, @AuthenticationPrincipal UserInfoDetails userInfoDetails, HttpServletRequest httpServletRequest) {
        ApiResponse<Void> apiResponse = userService.logout(userInfoDetails.getUsername(), new LogoutRequestDto(deviceId), httpServletRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Fetch logged in devices",
            description = "Fetch the logged in devices for a particular user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/logged-in-devices")
    public ResponseEntity<ApiResponse<List<LoggedInDevicesResponseDto>>> loggedInDevices(@AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<List<LoggedInDevicesResponseDto>> apiResponse = userService.loggedInDevices(userInfoDetails.getUsername());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Logout from a device",
            description = "Logout from a particular device for an user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(path = "/logout-device")
    public ResponseEntity<ApiResponse<Void>> logoutDevices(@CookieValue(value = "deviceId", required = true) String deviceId, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<Void> apiResponse = userService.logoutDevices(UUID.fromString(deviceId), userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Login API for remember me",
            description = "Login API for remember me activated devices"
    )
    @PostMapping(path = "/remember-me-login")
    public ResponseEntity<ApiResponse<UserLoginResponseDto>> rememberMeLogin(@CookieValue(value = "rememberMeToken", required = true) String rememberMeToken, @CookieValue(value = "deviceId", required = true) String deviceId, HttpServletRequest httpServletRequest) {
        JwtTokensDto jwtTokensDto = userService.rememberMeLogin(new RememberMeLoginRequestDto(deviceId, rememberMeToken), httpServletRequest);
        return LoginResponseCreator.createLoginResponse(jwtTokensDto);
    }

    @Operation(
            summary = "Check username existence",
            description = "Check the existence of an username in the system",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(path = "/check-username-existence")
    public ResponseEntity<ApiResponse<String>> checkUsernameExistence(@RequestParam String username, @AuthenticationPrincipal UserInfoDetails userInfoDetails) {
        ApiResponse<String> apiResponse = userService.checkUsernameExistence(username, userInfoDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
package com.projectsbynipin.todo_app_backend.utility;

import com.projectsbynipin.todo_app_backend.dto.ApiResponse;
import com.projectsbynipin.todo_app_backend.dto.JwtTokensDto;
import com.projectsbynipin.todo_app_backend.dto.UserLoginResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

public class LoginResponseCreator {

    private LoginResponseCreator() {
    }

    public static ResponseEntity<ApiResponse<UserLoginResponseDto>> createLoginResponse(JwtTokensDto jwtTokensDto) {
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", jwtTokensDto.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite(Constants.Miscellaneous.LAX)
                .maxAge(10 * 60L)
                .build();
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        ResponseCookie rememberMeCookie = null;
        boolean rememberMeEnabled = false;
        if (jwtTokensDto.rememberMeToken() != null) {
            rememberMeCookie = ResponseCookie.from("rememberMeToken", jwtTokensDto.rememberMeToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite(Constants.Miscellaneous.LAX)
                    .maxAge(7 * 24 * 60 * 60L)
                    .build();
            bodyBuilder.header(HttpHeaders.SET_COOKIE, rememberMeCookie.toString());
            rememberMeEnabled = true;
        }
        ResponseCookie deviceId = ResponseCookie.from("deviceId", jwtTokensDto.deviceId())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite(Constants.Miscellaneous.LAX)
                .maxAge(10 * 60L)
                .build();
        bodyBuilder.header(HttpHeaders.SET_COOKIE, deviceId.toString());
        ApiResponse<UserLoginResponseDto> apiResponse = ApiResponseCreator.success(Constants.Login.LOGIN_SUCCESSFUL, new UserLoginResponseDto(jwtTokensDto.jwtToken(), rememberMeEnabled), HttpStatus.OK);
        return bodyBuilder.body(apiResponse);
    }
}

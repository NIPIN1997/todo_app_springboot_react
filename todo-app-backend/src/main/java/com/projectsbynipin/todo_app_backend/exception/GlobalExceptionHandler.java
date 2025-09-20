package com.projectsbynipin.todo_app_backend.exception;

import com.projectsbynipin.todo_app_backend.dto.ApiResponse;
import com.projectsbynipin.todo_app_backend.utility.ApiResponseCreator;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        ApiResponse<Void> apiResponse = ApiResponseCreator.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException ex) {
        ApiResponse<Void> apiResponse = ApiResponseCreator.error(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JwtRefreshTokenExpiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwtRefreshTokenExpiredException(JwtRefreshTokenExpiredException ex) {
        ApiResponse<Void> apiResponse = ApiResponseCreator.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FailedToSaveRoleException.class)
    public ResponseEntity<ApiResponse<Void>> handleFailedToSaveRoleException(FailedToSaveRoleException ex) {
        ApiResponse<Void> apiResponse = ApiResponseCreator.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                e -> errors.put(e.getField(), e.getDefaultMessage())
        );
        ApiResponse<Map<String, String>> apiResponse = ApiResponseCreator.error(Constants.Miscellaneous.VALIDATION_FAILED, errors, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleRoleAlreadyExistsException(RoleAlreadyExistsException ex) {
        ApiResponse<Void> apiResponse = ApiResponseCreator.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserEmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserEmailAlreadyExistsException(UserEmailAlreadyExistsException ex) {
        ApiResponse<Void> apiResponse = ApiResponseCreator.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FailedToSaveUserException.class)
    public ResponseEntity<ApiResponse<Void>> handleFailedToSaveUserException(FailedToSaveUserException ex) {
        ApiResponse<Void> apiResponse = ApiResponseCreator.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ApiResponse<Void>> handleLoginFailedException(LoginFailedException ex) {
        ApiResponse<Void> apiResponse = ApiResponseCreator.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}

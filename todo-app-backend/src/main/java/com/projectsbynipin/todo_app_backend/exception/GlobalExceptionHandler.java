package com.projectsbynipin.todo_app_backend.exception;

import com.projectsbynipin.todo_app_backend.dto.ApiResponse;
import com.projectsbynipin.todo_app_backend.service.logging.ErrorLoggingService;
import com.projectsbynipin.todo_app_backend.utility.ApiResponseCreator;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorLoggingService errorLoggingService;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        errorLoggingService.log(ex);
        ApiResponse<Void> apiResponse = ApiResponseCreator.error(Constants.Miscellaneous.AN_UNEXCEPTED_ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        ApiResponse<Void> apiResponse = ApiResponseCreator.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(
            {
                    UserNotFoundException.class,
                    InvitationNotFoundException.class,
                    DashboardNotFoundException.class,
                    TaskNotFoundException.class,
                    DashboardColumnNotFoundException.class
            }
    )
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(RuntimeException ex) {
        ApiResponse<Void> apiResponse = ApiResponseCreator.error(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(
            {
                    JwtRefreshTokenExpiredException.class,
                    FailedToSaveRoleException.class,
                    RoleAlreadyExistsException.class,
                    UserEmailAlreadyExistsException.class,
                    FailedToSaveUserException.class,
                    FailedToRefreshTokenException.class,
                    FailedToEditUserException.class,
                    LogoutFailedException.class,
                    FailedToRetrieveDevicesException.class,
                    FailedToLogoutDeviceException.class,
                    FailedToCreateDashboardException.class,
                    FailedToCreateDashboardColumnException.class,
                    FailedToRetrieveDashboardsException.class,
                    FailedToCheckUsernameException.class,
                    FailedToFetchInvitationsException.class,
                    FailedToAcceptOrRejectInvitationException.class,
                    FailedToRetrieveDashboardException.class,
                    FailedToRetrieveDashboardColumnNamesException.class,
                    FailedToRetrieveDashboardMemberNamesException.class,
                    FailedToAddTaskException.class,
                    FailedToUpdateTaskStatusException.class,
                    FailedToFetchTaskException.class,
                    FailedToDeleteDashboardException.class,
                    FailedToArchiveDashboardException.class,
                    FailedToDeleteTaskException.class,
                    FailedToEditTaskException.class,
                    FailedToCreateInvitationException.class,
                    FailedToUnarchiveDashboardException.class,
                    FailedToEditDashboardException.class,
                    FailedToRemoveDashboardMemberException.class,
                    FailedToAddDashboardMemberException.class,
                    FailedToEditDashboardColumnNameException.class
            }
    )
    public ResponseEntity<ApiResponse<Void>> handleInternalServerErrors(RuntimeException ex) {
        errorLoggingService.log(ex);
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

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ApiResponse<Void>> handleLoginFailedException(LoginFailedException ex) {
        ApiResponse<Void> apiResponse = ApiResponseCreator.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FailedLoginRateLimitReachedException.class)
    public ResponseEntity<ApiResponse<Void>> handleFailedLoginRateLimitReachedException(FailedLoginRateLimitReachedException ex) {
        ApiResponse<Void> apiResponse = ApiResponseCreator.error(ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
        return new ResponseEntity<>(apiResponse, HttpStatus.TOO_MANY_REQUESTS);
    }

}
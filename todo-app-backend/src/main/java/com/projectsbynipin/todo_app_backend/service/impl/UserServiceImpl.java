package com.projectsbynipin.todo_app_backend.service.impl;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.entity.Role;
import com.projectsbynipin.todo_app_backend.entity.User;
import com.projectsbynipin.todo_app_backend.enums.Status;
import com.projectsbynipin.todo_app_backend.exception.FailedToSaveUserException;
import com.projectsbynipin.todo_app_backend.exception.LoginFailedException;
import com.projectsbynipin.todo_app_backend.exception.UserEmailAlreadyExistsException;
import com.projectsbynipin.todo_app_backend.exception.UserNotFoundException;
import com.projectsbynipin.todo_app_backend.mapper.UserMapper;
import com.projectsbynipin.todo_app_backend.repository.RoleRepository;
import com.projectsbynipin.todo_app_backend.repository.UserRepository;
import com.projectsbynipin.todo_app_backend.service.UserService;
import com.projectsbynipin.todo_app_backend.service.jwt.JwtService;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> addAdmin(AddUserRequestDto addUserRequestDto) {
        User user = userRepository.findByEmailAndIsDeleted(addUserRequestDto.email(), false);
        if (user != null) {
            throw new UserEmailAlreadyExistsException(Constants.USER_EMAIL_ALREADY_EXISTS_EXCEPTION);
        }
        Role role = roleRepository.findByName("ROLE_ADMIN");
        try {
            userRepository.save(userMapper.addUserRequestDtoToUser(addUserRequestDto, role));
            ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                    .status(Status.SUCCESS)
                    .message(Constants.ADMIN_SAVED)
                    .time(LocalDateTime.now())
                    .httpStatus(HttpStatus.CREATED)
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new FailedToSaveUserException(Constants.FAILED_TO_SAVE_ADMIN);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> addUser(AddUserRequestDto addUserRequestDto) {
        User user = userRepository.findByEmailAndIsDeleted(addUserRequestDto.email(), false);
        if (user != null) {
            logger.error("Failed to add user with email : {} since user already exists.", addUserRequestDto.email());
            throw new UserEmailAlreadyExistsException(Constants.USER_EMAIL_ALREADY_EXISTS_EXCEPTION);
        }
        Role role = roleRepository.findByName("ROLE_USER");
        try {
            User user1 = userRepository.save(userMapper.addUserRequestDtoToUser(addUserRequestDto, role));
            logger.info("New user added with ID : {} and email : {}.", user1.getId(), user1.getEmail());
            ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                    .status(Status.SUCCESS)
                    .message(Constants.USER_SAVED)
                    .httpStatus(HttpStatus.CREATED)
                    .time(LocalDateTime.now())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to add user with email : {}.", addUserRequestDto.email(), e);
            throw new FailedToSaveUserException(Constants.FAILED_TO_SAVE_USER);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.email(), loginRequestDto.password()
                )
        );
        if (authentication.isAuthenticated()) {
            User user = userRepository.findByEmailAndIsDeleted(loginRequestDto.email(), false);
            logger.info("Login -> User ID: {} , email : {}.", user.getId(), user.getEmail());
            LoginResponseDto loginResponseDto = new LoginResponseDto(
                    jwtService.generateToken(loginRequestDto.email()),
                    jwtService.generateRefreshToken(loginRequestDto.email())
            );
            ApiResponse<LoginResponseDto> apiResponse = ApiResponse.<LoginResponseDto>builder()
                    .status(Status.SUCCESS)
                    .message(Constants.LOGIN_SUCCESSFUL)
                    .data(loginResponseDto)
                    .httpStatus(HttpStatus.OK)
                    .time(LocalDateTime.now())
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } else {
            logger.error("Failed login attempt -> Email : {}", loginRequestDto.email());
            throw new LoginFailedException(Constants.LOGIN_FAILED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<ViewUserResponseDto>> getUser(UUID userId, UserInfoDetails userInfoDetails) {
        User user1 = userRepository.findByEmailAndIsDeleted(userInfoDetails.getUsername(), false);
        User user2 = userRepository.findById(userId).orElse(null);
        if (user1 == null || user2 == null) {
            logger.warn("User with ID : {} not found.", userId);
            throw new UserNotFoundException(Constants.USER_NOT_FOUND);
        }
        if (!user1.getId().equals(user2.getId())) {
            logger.warn("Forbidden access -> Email : {}", userInfoDetails.getUsername());
            throw new AccessDeniedException(Constants.ACCESS_DENIED);
        }
        ApiResponse<ViewUserResponseDto> apiResponse = ApiResponse.<ViewUserResponseDto>builder()
                .status(Status.SUCCESS)
                .message(Constants.USER_RETRIEVED)
                .data(userMapper.userToViewUserResponseDto(user2))
                .httpStatus(HttpStatus.OK)
                .time(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}

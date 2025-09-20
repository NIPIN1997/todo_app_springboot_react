package com.projectsbynipin.todo_app_backend.service.impl;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.entity.Role;
import com.projectsbynipin.todo_app_backend.entity.User;
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
import com.projectsbynipin.todo_app_backend.utility.ApiResponseCreator;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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
    public ApiResponse<Void> createAdmin(AddUserRequestDto addUserRequestDto) {
        User user = userRepository.findByEmailAndIsDeleted(addUserRequestDto.email(), false);
        if (user != null) {
            throw new UserEmailAlreadyExistsException(Constants.User.USER_EMAIL_ALREADY_EXISTS);
        }
        Role role = roleRepository.findByName(Constants.Role.ROLE_ADMIN);
        try {
            userRepository.save(userMapper.addUserRequestDtoToUser(addUserRequestDto, role));
            return ApiResponseCreator.success(Constants.User.ADMIN_CREATED, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new FailedToSaveUserException(Constants.User.FAILED_TO_CREATE_ADMIN);
        }
    }

    @Override
    public ApiResponse<Void> createUser(AddUserRequestDto addUserRequestDto) {
        User user = userRepository.findByEmailAndIsDeleted(addUserRequestDto.email(), false);
        if (user != null) {
            logger.error("Failed to add user with email : {} since user already exists.", addUserRequestDto.email());
            throw new UserEmailAlreadyExistsException(Constants.User.USER_EMAIL_ALREADY_EXISTS);
        }
        Role role = roleRepository.findByName(Constants.Role.ROLE_USER);
        try {
            User user1 = userRepository.save(userMapper.addUserRequestDtoToUser(addUserRequestDto, role));
            logger.info("New user added with ID : {} and email : {}.", user1.getId(), user1.getEmail());
            return ApiResponseCreator.success(Constants.User.USER_CREATED, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to add user with email : {}.", addUserRequestDto.email(), e);
            throw new FailedToSaveUserException(Constants.User.FAILED_TO_CREATE_USER);
        }
    }

    @Override
    public ApiResponse<LoginResponseDto> login(LoginRequestDto loginRequestDto) {
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
            return ApiResponseCreator.success(Constants.Login.LOGIN_SUCCESSFUL, loginResponseDto, HttpStatus.OK);
        } else {
            logger.error("Failed login attempt -> Email : {}", loginRequestDto.email());
            throw new LoginFailedException(Constants.Login.LOGIN_FAILED);
        }
    }

    @Override
    public ApiResponse<ViewUserResponseDto> getUser(UUID userId, UserInfoDetails userInfoDetails) {
        User user1 = userRepository.findByEmailAndIsDeleted(userInfoDetails.getUsername(), false);
        User user2 = userRepository.findById(userId).orElse(null);
        if (user1 == null || user2 == null) {
            logger.warn("User with ID : {} not found.", userId);
            throw new UserNotFoundException(Constants.User.USER_NOT_FOUND);
        }
        if (!user1.getId().equals(user2.getId())) {
            logger.warn("Forbidden access -> Email : {}", userInfoDetails.getUsername());
            throw new AccessDeniedException(Constants.Miscellaneous.ACCESS_DENIED);
        }
        return ApiResponseCreator.success(Constants.User.USER_RETRIEVED, userMapper.userToViewUserResponseDto(user2), HttpStatus.OK);
    }
}

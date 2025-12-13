package com.projectsbynipin.todo_app_backend.service.impl;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.entity.Role;
import com.projectsbynipin.todo_app_backend.entity.User;
import com.projectsbynipin.todo_app_backend.entity.UserSession;
import com.projectsbynipin.todo_app_backend.exception.*;
import com.projectsbynipin.todo_app_backend.mapper.UserMapper;
import com.projectsbynipin.todo_app_backend.repository.RoleRepository;
import com.projectsbynipin.todo_app_backend.repository.UserRepository;
import com.projectsbynipin.todo_app_backend.repository.UserSessionRepository;
import com.projectsbynipin.todo_app_backend.service.UserService;
import com.projectsbynipin.todo_app_backend.service.encryption.EncryptionService;
import com.projectsbynipin.todo_app_backend.service.encryption.HashingService;
import com.projectsbynipin.todo_app_backend.service.jwt.JwtService;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import com.projectsbynipin.todo_app_backend.service.redis.RedisService;
import com.projectsbynipin.todo_app_backend.utility.ApiResponseCreator;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ua_parser.Client;
import ua_parser.Parser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final EncryptionService encryptionService;
    private final UserSessionRepository userSessionRepository;
    private final HashingService hashingService;

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper, AuthenticationManager authenticationManager, JwtService jwtService, RedisService redisService, EncryptionService encryptionService, UserSessionRepository userSessionRepository, HashingService hashingService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.redisService = redisService;
        this.encryptionService = encryptionService;
        this.userSessionRepository = userSessionRepository;
        this.hashingService = hashingService;
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
    public ApiResponse<LoginResponseDto> login(LoginRequestDto loginRequestDto, HttpServletRequest httpServletRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.email(), loginRequestDto.password()
                )
        );
        if (authentication.isAuthenticated()) {
            User user = userRepository.findByEmailAndIsDeleted(loginRequestDto.email(), false);
            List<UserSession> userSessionList = userSessionRepository.findByUserIdAndIsActive(user.getId(), true);
            if (userSessionList.size() <= 1) {
                logger.info("Login -> User ID: {} , email : {}.", user.getId(), user.getEmail());
                UUID deviceId = UUID.randomUUID();
                String jwtToken = jwtService.generateToken(loginRequestDto.email(), deviceId);
                String jwtRefreshToken = jwtService.generateRefreshToken(loginRequestDto.email(), deviceId);
                Parser parser = new Parser();
                Client client = parser.parse(httpServletRequest.getHeader("User-Agent"));
                UserSession userSession = new UserSession();
                userSession.setDeviceId(deviceId);
                userSession.setUserId(user.getId());
                userSession.setBrowser(client.userAgent.family);
                userSession.setOs(client.os.family);
                userSession.setOsVersion(client.os.major);
                userSession.setFingerprint(hashingService.hashDeviceId(client, userSession.getDeviceId()));
                userSession.setLastUpdated(LocalDateTime.now());
                if (loginRequestDto.rememberMe()) {
                    userSession.setRememberMe(true);
                    userSession.setRememberMeToken(jwtService.generateRememberMeToken(loginRequestDto.email(), deviceId));
                }
                UserSession session = null;
                try {
                    session = userSessionRepository.save(userSession);
                    redisService.storeRefreshToken(loginRequestDto.email(), session.getDeviceId(), encryptionService.getEncryptedToken(jwtRefreshToken));
                } catch (Exception e) {
                    throw new LoginFailedException(Constants.Login.LOGIN_FAILED);
                }
                return ApiResponseCreator.success(Constants.Login.LOGIN_SUCCESSFUL, new LoginResponseDto(jwtToken, jwtRefreshToken, session.getDeviceId().toString(), session.getRememberMeToken()), HttpStatus.OK);
            } else {
                logger.error("Failed login attempt -> Email : {}", loginRequestDto.email());
                throw new LoginFailedException(Constants.Login.LOGIN_DEVICE_LIMIT_REACHED);
            }
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

    @Override
    public ApiResponse<LoginResponseDto> refreshToken(RefreshTokenRequestDto refreshTokenRequestDto, HttpServletRequest httpServletRequest) {
        UUID deviceID = UUID.fromString(refreshTokenRequestDto.deviceId());
        UserSession userSession = userSessionRepository.findByDeviceId(deviceID);
        Parser parser = new Parser();
        Client client = parser.parse(httpServletRequest.getHeader("User-Agent"));
        String username = jwtService.extractUsername(refreshTokenRequestDto.refreshToken());
        if (userSession != null && userSession.isActive() && userSession.getFingerprint().equals(hashingService.hashDeviceId(client, deviceID))) {
            boolean isValid = jwtService.validateRefreshToken(refreshTokenRequestDto.refreshToken(), userSession.getDeviceId());
            if (isValid) {
                User user = userRepository.findByEmailAndIsDeleted(username, false);
                logger.info("Issued refresh token -> User ID: {} , email : {}.", user.getId(), user.getEmail());
                String jwtToken = jwtService.generateToken(username, deviceID);
                String jwtRefreshToken = jwtService.generateRefreshToken(username, deviceID);
                redisService.storeRefreshToken(username, userSession.getDeviceId(), encryptionService.getEncryptedToken(jwtRefreshToken));
                return ApiResponseCreator.success(Constants.Login.LOGIN_SUCCESSFUL, new LoginResponseDto(jwtToken, jwtRefreshToken, null, null), HttpStatus.OK);
            } else {
                logger.error("Failed to issue refresh token -> Email : {}", username);
                redisService.deleteRefreshToken(username, userSession.getDeviceId());
                userSession.setActive(false);
                userSessionRepository.save(userSession);
                throw new FailedToRefreshTokenException(Constants.Miscellaneous.FAILED_TO_REFRESH_TOKEN);
            }
        } else {
            if (userSession != null) {
                redisService.deleteRefreshToken(username, userSession.getDeviceId());
                userSession.setActive(false);
                userSessionRepository.save(userSession);
            }
            logger.error("Failed to issue refresh token -> Email : {}", username);
            throw new FailedToRefreshTokenException(Constants.Miscellaneous.FAILED_TO_REFRESH_TOKEN);
        }
    }

    @Override
    public ApiResponse<Void> editUser(UUID userId, EditUserRequestDto editUserRequestDto, UserInfoDetails userInfoDetails) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UserNotFoundException(Constants.User.USER_NOT_FOUND);
        }
        User user1 = userRepository.findByEmailAndIsDeleted(userInfoDetails.getUsername(), false);
        if (!user.getId().equals(user1.getId())) {
            logger.warn("Forbidden access -> Email : {}", userInfoDetails.getUsername());
            throw new AccessDeniedException(Constants.Miscellaneous.ACCESS_DENIED);
        }
        if (!editUserRequestDto.name().isBlank()) {
            user.setName(editUserRequestDto.name());
        }
        if (!editUserRequestDto.contact().isBlank()) {
            user.setContact(editUserRequestDto.contact());
        }
        try {
            userRepository.save(user);
            logger.info("Edited account -> User ID: {}.", user.getId());
            return ApiResponseCreator.success(Constants.User.USER_EDITED, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to edit account -> User ID: {}.", userId);
            throw new FailedToEditUserException(Constants.User.FAILED_TO_EDIT_USER);
        }
    }

    @Override
    public ApiResponse<Void> logout(String username, LogoutRequestDto logoutRequestDto) {
        try {
            User user = userRepository.findByEmailAndIsDeleted(username, false);
            UserSession userSession = userSessionRepository.findByDeviceId(UUID.fromString(logoutRequestDto.deviceId()));
            redisService.deleteRefreshToken(username, userSession.getDeviceId());
            if (userSession != null) {
                logger.info("Logout -> User ID: {} , email : {}.", user.getId(), user.getEmail());
                userSession.setActive(false);
                userSession.setLogoutTime(LocalDateTime.now());
                userSessionRepository.save(userSession);
                return ApiResponseCreator.success(Constants.Login.LOGOUT_SUCCESSFUL, HttpStatus.OK);
            } else {
                logger.error("Failed logout attempt -> Email : {}", username);
                throw new LogoutFailedException(Constants.Login.LOGOUT_FAILED);
            }
        } catch (Exception e) {
            logger.error("Failed logout attempt -> Email : {}", username);
            throw new LogoutFailedException(Constants.Login.LOGOUT_FAILED);
        }
    }

    @Override
    public ApiResponse<List<LoggedInDevicesResponseDto>> loggedInDevices(UUID userId, String username) {
        try {
            User user = userRepository.findByEmailAndIsDeleted(username, false);
            if (!userId.equals(user.getId())) {
                logger.warn("Forbidden access -> Email : {}", username);
                throw new AccessDeniedException(Constants.Miscellaneous.ACCESS_DENIED);
            }
            List<UserSession> userSessions = userSessionRepository.findByUserIdAndIsActive(userId, true);
            return ApiResponseCreator.success(Constants.User.LOGGED_IN_DEVICES_RETRIEVED, userMapper.userSessionsToLoggedInDevicesResponseDto(userSessions), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve logged in devices -> Email : {}", username);
            throw new FailedToRetrieveDevicesException(Constants.User.FAILED_TO_RETRIEVE_LOGGED_IN_DEVICES);
        }
    }

    @Override
    public ApiResponse<Void> logoutDevices(UUID deviceId, UserInfoDetails userInfoDetails) {
        try {
            User user = userRepository.findByEmailAndIsDeleted(userInfoDetails.getUsername(), false);
            UserSession userSession = userSessionRepository.findByDeviceId(deviceId);
            if (!user.getId().equals(userSession.getUserId())) {
                logger.warn("Forbidden access -> Email : {}", userInfoDetails.getUsername());
                throw new AccessDeniedException(Constants.Miscellaneous.ACCESS_DENIED);
            }
            userSession.setActive(false);
            userSession.setLogoutTime(LocalDateTime.now());
            userSessionRepository.save(userSession);
            redisService.deleteRefreshToken(userInfoDetails.getUsername(), deviceId);
            return ApiResponseCreator.success(Constants.Login.DEVICE_LOGGED_OUT, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to logout device -> Email : {}, Device : {}", userInfoDetails.getUsername(), deviceId);
            throw new FailedToLogoutDeviceException(Constants.Login.DEVICE_LOG_OUT_FAILED);
        }
    }

    @Override
    public ApiResponse<LoginResponseDto> rememberMeLogin(RememberMeLoginRequestDto rememberMeLoginRequestDto, HttpServletRequest httpServletRequest) {
        try {
            UserSession userSession = userSessionRepository.findByDeviceId(UUID.fromString(rememberMeLoginRequestDto.deviceId()));
            Parser parser = new Parser();
            Client client = parser.parse(httpServletRequest.getHeader("User-Agent"));
            String fingerprint = hashingService.hashDeviceId(client, UUID.fromString(rememberMeLoginRequestDto.deviceId()));
            if (userSession == null) {
                throw new LoginFailedException(Constants.Login.LOGIN_FAILED);
            }
            if (!jwtService.isTokenExpired(userSession.getRememberMeToken()) && userSession.getRememberMeToken().equals(rememberMeLoginRequestDto.rememberMeToken()) && userSession.getFingerprint().equals(fingerprint) && userSession.isActive()) {
                User user = userRepository.findById(userSession.getUserId()).orElseThrow(() -> new UserNotFoundException(Constants.User.USER_NOT_FOUND));
                String jwtToken = jwtService.generateToken(user.getEmail(), UUID.fromString(rememberMeLoginRequestDto.deviceId()));
                String jwtRefreshToken = jwtService.generateRefreshToken(user.getEmail(), UUID.fromString(rememberMeLoginRequestDto.deviceId()));
                userSession.setLastUpdated(LocalDateTime.now());
                String rememberMeToken = jwtService.generateRememberMeToken(user.getEmail(), UUID.fromString(rememberMeLoginRequestDto.deviceId()));
                userSession.setRememberMeToken(rememberMeToken);
                userSessionRepository.save(userSession);
                redisService.storeRefreshToken(user.getEmail(), userSession.getDeviceId(), encryptionService.getEncryptedToken(jwtRefreshToken));
                logger.info("Login -> User ID: {} , email : {}.", user.getId(), user.getEmail());
                return ApiResponseCreator.success(Constants.Login.LOGIN_SUCCESSFUL, new LoginResponseDto(jwtToken, jwtRefreshToken, userSession.getDeviceId().toString(), rememberMeToken), HttpStatus.OK);
            } else {
                logger.error("Failed login attempt");
                throw new LoginFailedException(Constants.Login.LOGIN_FAILED);
            }
        } catch (Exception e) {
            logger.error("Failed login attempt: {}", e.getMessage());
            throw new LoginFailedException(Constants.Login.LOGIN_FAILED);
        }
    }

}
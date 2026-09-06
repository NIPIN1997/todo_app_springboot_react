package com.projectsbynipin.todo_app_backend.service.impl;

import com.projectsbynipin.todo_app_backend.dto.*;
import com.projectsbynipin.todo_app_backend.dto.loggingdtos.LoginLogoutActivityLog;
import com.projectsbynipin.todo_app_backend.dto.loggingdtos.TokenRefreshActivityLog;
import com.projectsbynipin.todo_app_backend.entity.Role;
import com.projectsbynipin.todo_app_backend.entity.User;
import com.projectsbynipin.todo_app_backend.entity.UserSession;
import com.projectsbynipin.todo_app_backend.enums.LoginLogout;
import com.projectsbynipin.todo_app_backend.exception.*;
import com.projectsbynipin.todo_app_backend.mapper.UserMapper;
import com.projectsbynipin.todo_app_backend.repository.RoleRepository;
import com.projectsbynipin.todo_app_backend.repository.UserRepository;
import com.projectsbynipin.todo_app_backend.repository.UserSessionRepository;
import com.projectsbynipin.todo_app_backend.service.DashboardService;
import com.projectsbynipin.todo_app_backend.service.UserService;
import com.projectsbynipin.todo_app_backend.service.encryption.EncryptionService;
import com.projectsbynipin.todo_app_backend.service.encryption.HashingService;
import com.projectsbynipin.todo_app_backend.service.jwt.JwtService;
import com.projectsbynipin.todo_app_backend.service.jwt.UserInfoDetails;
import com.projectsbynipin.todo_app_backend.service.kafka.KafkaProducerService;
import com.projectsbynipin.todo_app_backend.service.redis.RedisService;
import com.projectsbynipin.todo_app_backend.utility.ApiResponseCreator;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ua_parser.Client;
import ua_parser.Parser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
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
    private final DashboardService dashboardService;
    private final KafkaProducerService kafkaProducerService;
    @Value("${security.jwt.refresh-token-secret-key}")
    private String refreshTokenSecretKey;

    private User findUserByEmailAndDeleted(String email) {
        User user = userRepository.findByEmailAndDeleted(email, false);
        if (user == null) {
            throw new UserNotFoundException(Constants.User.USER_NOT_FOUND);
        }
        return user;
    }

    private void checkUserEmailNotExists(String email) {
        User user = userRepository.findByEmailAndDeleted(email, false);
        if (user != null) {
            throw new UserEmailAlreadyExistsException(Constants.User.USER_EMAIL_ALREADY_EXISTS);
        }
    }

    @Override
    public ApiResponse<Void> createAdmin(AddUserRequestDto addUserRequestDto) {
        checkUserEmailNotExists(addUserRequestDto.email());
        try {
            Role role = roleRepository.findByName(Constants.Role.ROLE_ADMIN);
            userRepository.save(userMapper.addUserRequestDtoToUser(addUserRequestDto, role));
            return ApiResponseCreator.success(Constants.User.ADMIN_CREATED, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new FailedToSaveUserException(Constants.User.FAILED_TO_CREATE_ADMIN, e);
        }
    }

    @Override
    public ApiResponse<Void> createUser(AddUserRequestDto addUserRequestDto) {
        checkUserEmailNotExists(addUserRequestDto.email());
        try {
            Role role = roleRepository.findByName(Constants.Role.ROLE_USER);
            User user1 = userRepository.save(userMapper.addUserRequestDtoToUser(addUserRequestDto, role));
            dashboardService.createDefaultDashboardForUser(user1);
            return ApiResponseCreator.success(Constants.User.USER_CREATED, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new FailedToSaveUserException(Constants.User.FAILED_TO_CREATE_USER, e);
        }
    }

    @Override
    public JwtTokensDto login(LoginRequestDto loginRequestDto, HttpServletRequest httpServletRequest) {
        if (redisService.checkIfUserLockedOut(loginRequestDto.email())) {
            throw new FailedLoginRateLimitReachedException(Constants.Login.FAILED_LOGIN_LIMIT_REACHED + redisService.getRemainingLoginLockoutTime(loginRequestDto.email()));
        }
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.email(), loginRequestDto.password()
                    )
            );
        } catch (BadCredentialsException e) {
            redisService.addLoginLockoutAttempts(loginRequestDto.email());
            throw new BadCredentialsException(Constants.Login.INVALID_CREDENTIALS+redisService.getRemainingLoginAttempts(loginRequestDto.email()));
        }
        if (authentication != null && authentication.isAuthenticated()) {
            User user = findUserByEmailAndDeleted(loginRequestDto.email());
            List<UserSession> userSessionList = userSessionRepository.findByUserIdAndActive(user.getId(), true);
            if (userSessionList.size() <= 1) {
                UUID deviceId = UUID.randomUUID();
                String jwtToken = jwtService.generateToken(user, deviceId);
                String jwtRefreshToken = jwtService.generateRefreshToken(user, deviceId);
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
                    userSession.setRememberMeToken(jwtService.generateRememberMeToken(user, deviceId));
                }
                UserSession session = null;
                String redisKey = Constants.Redis.REDIS_KEY_PREFIX_TOKEN_PREFIX + loginRequestDto.email() + deviceId;
                userSession.setRedisKey(redisKey);
                try {
                    session = userSessionRepository.save(userSession);
                    redisService.storeRefreshToken(loginRequestDto.email(), session.getDeviceId(), encryptionService.getEncryptedToken(jwtRefreshToken));
                } catch (Exception e) {
                    throw new LoginFailedException(Constants.Login.LOGIN_FAILED, e);
                }
                redisService.clearLoginAttempts(loginRequestDto.email());
                kafkaProducerService.sendMessage(
                        Constants.KafkaTopics.LOGIN_ACTIVITY_LOGS,
                        LoginLogoutActivityLog.builder()
                                .username(user.getEmail())
                                .action(LoginLogout.LOGIN)
                                .browserName(client.userAgent.family)
                                .time(LocalDateTime.now())
                                .build()
                );
                return new JwtTokensDto(jwtToken, jwtRefreshToken, session.getRememberMeToken(), session.getDeviceId().toString());
            } else {
                throw new LoginFailedException(Constants.Login.LOGIN_DEVICE_LIMIT_REACHED);
            }
        } else {
            throw new LoginFailedException(Constants.Login.LOGIN_FAILED);
        }
    }

    @Override
    public ApiResponse<ViewUserResponseDto> getUser(UserInfoDetails userInfoDetails) {
        User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
        return ApiResponseCreator.success(Constants.User.USER_RETRIEVED, userMapper.userToViewUserResponseDto(user), HttpStatus.OK);
    }

    @Override
    public JwtTokensDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto, HttpServletRequest httpServletRequest) {
        UUID deviceID = UUID.fromString(refreshTokenRequestDto.deviceId());
        UserSession userSession = userSessionRepository.findByDeviceId(deviceID);
        Parser parser = new Parser();
        Client client = parser.parse(httpServletRequest.getHeader("User-Agent"));
        String username = jwtService.extractUsername(refreshTokenRequestDto.refreshToken(), refreshTokenSecretKey);
        if (userSession != null && userSession.isActive() && userSession.getFingerprint().equals(hashingService.hashDeviceId(client, deviceID))) {
            boolean isValid = jwtService.validateRefreshToken(refreshTokenRequestDto.refreshToken(), userSession.getDeviceId());
            if (isValid) {
                User user = userRepository.findByEmailAndDeleted(username, false);
                String jwtToken = jwtService.generateToken(user, deviceID);
                String jwtRefreshToken = jwtService.generateRefreshToken(user, deviceID);
                redisService.storeRefreshToken(username, userSession.getDeviceId(), encryptionService.getEncryptedToken(jwtRefreshToken));
                kafkaProducerService.sendMessage(
                        Constants.KafkaTopics.TOKEN_REFRESH_ACTIVITY_LOGS,
                        TokenRefreshActivityLog.builder()
                                .username(user.getEmail())
                                .time(LocalDateTime.now())
                                .build()
                );
                return new JwtTokensDto(jwtToken, jwtRefreshToken, null, deviceID.toString());
            } else {
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
            throw new FailedToRefreshTokenException(Constants.Miscellaneous.FAILED_TO_REFRESH_TOKEN);
        }
    }

    @Override
    public ApiResponse<Void> editUser(EditUserRequestDto editUserRequestDto, UserInfoDetails userInfoDetails) {
        try {
            User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
            if (!editUserRequestDto.name().isBlank()) {
                user.setName(editUserRequestDto.name());
            }
            if (!editUserRequestDto.contact().isBlank()) {
                user.setContact(editUserRequestDto.contact());
            }
            userRepository.save(user);
            return ApiResponseCreator.success(Constants.User.USER_EDITED, HttpStatus.OK);
        } catch (Exception e) {
            throw new FailedToEditUserException(Constants.User.FAILED_TO_EDIT_USER, e);
        }
    }

    @Override
    public ApiResponse<Void> logout(String username, LogoutRequestDto logoutRequestDto, HttpServletRequest httpServletRequest) {
        try {
            UserSession userSession = userSessionRepository.findByDeviceId(UUID.fromString(logoutRequestDto.deviceId()));
            redisService.deleteRefreshToken(username, userSession.getDeviceId());
            Parser parser = new Parser();
            Client client = parser.parse(httpServletRequest.getHeader("User-Agent"));
            if (userSession != null) {
                userSession.setActive(false);
                userSession.setLogoutTime(LocalDateTime.now());
                userSessionRepository.save(userSession);
                kafkaProducerService.sendMessage(
                        Constants.KafkaTopics.LOGIN_ACTIVITY_LOGS,
                        LoginLogoutActivityLog.builder()
                                .username(username)
                                .browserName(client.userAgent.family)
                                .action(LoginLogout.LOGOUT)
                                .time(LocalDateTime.now())
                                .build()
                );
                return ApiResponseCreator.success(Constants.Login.LOGOUT_SUCCESSFUL, HttpStatus.OK);
            } else {
                throw new LogoutFailedException(Constants.Login.LOGOUT_FAILED);
            }
        } catch (Exception e) {
            throw new LogoutFailedException(Constants.Login.LOGOUT_FAILED, e);
        }
    }

    @Override
    public ApiResponse<List<LoggedInDevicesResponseDto>> loggedInDevices(String username) {
        try {
            User user = findUserByEmailAndDeleted(username);
            List<UserSession> userSessions = userSessionRepository.findByUserIdAndActive(user.getId(), true);
            return ApiResponseCreator.success(Constants.User.LOGGED_IN_DEVICES_RETRIEVED, userMapper.userSessionsToLoggedInDevicesResponseDto(userSessions), HttpStatus.OK);
        } catch (Exception e) {
            throw new FailedToRetrieveDevicesException(Constants.User.FAILED_TO_RETRIEVE_LOGGED_IN_DEVICES, e);
        }
    }

    @Override
    public ApiResponse<Void> logoutDevices(UUID deviceId, UserInfoDetails userInfoDetails) {
        try {
            User user = findUserByEmailAndDeleted(userInfoDetails.getUsername());
            UserSession userSession = userSessionRepository.findByDeviceId(deviceId);
            if (!user.getId().equals(userSession.getUserId())) {
                throw new AccessDeniedException(Constants.Miscellaneous.ACCESS_DENIED);
            }
            userSession.setActive(false);
            userSession.setLogoutTime(LocalDateTime.now());
            userSessionRepository.save(userSession);
            redisService.deleteRefreshToken(userInfoDetails.getUsername(), deviceId);
            return ApiResponseCreator.success(Constants.Login.DEVICE_LOGGED_OUT, HttpStatus.OK);
        } catch (Exception e) {
            throw new FailedToLogoutDeviceException(Constants.Login.DEVICE_LOG_OUT_FAILED, e);
        }
    }

    @Override
    public JwtTokensDto rememberMeLogin(RememberMeLoginRequestDto rememberMeLoginRequestDto, HttpServletRequest httpServletRequest) {
        try {
            UserSession userSession = userSessionRepository.findByDeviceId(UUID.fromString(rememberMeLoginRequestDto.deviceId()));
            Parser parser = new Parser();
            Client client = parser.parse(httpServletRequest.getHeader("User-Agent"));
            String fingerprint = hashingService.hashDeviceId(client, UUID.fromString(rememberMeLoginRequestDto.deviceId()));
            if (userSession == null) {
                throw new LoginFailedException(Constants.Login.LOGIN_FAILED);
            }
            if (!jwtService.isRememberMeTokenExpired(userSession.getRememberMeToken()) && userSession.getRememberMeToken().equals(rememberMeLoginRequestDto.rememberMeToken()) && userSession.getFingerprint().equals(fingerprint) && userSession.isActive()) {
                User user = userRepository.findById(userSession.getUserId()).orElseThrow(() -> new UserNotFoundException(Constants.User.USER_NOT_FOUND));
                String jwtToken = jwtService.generateToken(user, UUID.fromString(rememberMeLoginRequestDto.deviceId()));
                String jwtRefreshToken = jwtService.generateRefreshToken(user, UUID.fromString(rememberMeLoginRequestDto.deviceId()));
                userSession.setLastUpdated(LocalDateTime.now());
                String rememberMeToken = jwtService.generateRememberMeToken(user, UUID.fromString(rememberMeLoginRequestDto.deviceId()));
                userSession.setRememberMeToken(rememberMeToken);
                userSessionRepository.save(userSession);
                redisService.storeRefreshToken(user.getEmail(), userSession.getDeviceId(), encryptionService.getEncryptedToken(jwtRefreshToken));
                return new JwtTokensDto(jwtToken, jwtRefreshToken, rememberMeToken, userSession.getDeviceId().toString());
            } else {
                throw new LoginFailedException(Constants.Login.LOGIN_FAILED);
            }
        } catch (Exception e) {
            throw new LoginFailedException(Constants.Login.LOGIN_FAILED, e);
        }
    }

    @Override
    public ApiResponse<String> checkUsernameExistence(String username, UserInfoDetails userInfoDetails) {
        try {
            User userExists = userRepository.checkUsernameExistence(username);
            if (userExists != null) {
                if (userExists.getEmail().equals(userInfoDetails.getUsername())) {
                    return ApiResponseCreator.success(Constants.User.USERNAME_EXISTS, "Master is already a member.", HttpStatus.OK);
                } else {
                    return ApiResponseCreator.success(Constants.User.USERNAME_EXISTS, "true", HttpStatus.OK);
                }
            } else {
                return ApiResponseCreator.success(Constants.User.USERNAME_DOESNOT_EXIST, "false", HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new FailedToCheckUsernameException(Constants.User.USERNAME_DOESNOT_EXIST, e);
        }
    }
}
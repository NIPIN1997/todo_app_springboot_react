package com.projectsbynipin.todo_app_backend.service.scheduler;

import com.projectsbynipin.todo_app_backend.entity.User;
import com.projectsbynipin.todo_app_backend.entity.UserSession;
import com.projectsbynipin.todo_app_backend.exception.UserNotFoundException;
import com.projectsbynipin.todo_app_backend.repository.UserRepository;
import com.projectsbynipin.todo_app_backend.repository.UserSessionRepository;
import com.projectsbynipin.todo_app_backend.service.logging.ErrorLoggingService;
import com.projectsbynipin.todo_app_backend.service.redis.RedisService;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AutoLogoutSchedulerService {

    private final RedisService redisService;
    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;
    private final ErrorLoggingService errorLoggingService;

    @Scheduled(fixedRate = 360 * 60 * 1000)
    public void autoLogout() {
        try {
            List<UserSession> userSessionList = userSessionRepository.getAllActiveNotRememberedSessions();
            List<UserSession> newUserSessionList = new ArrayList<>();
            LocalDateTime today = LocalDateTime.now();
            for (UserSession userSession : userSessionList) {
                User user = userRepository.findById(userSession.getUserId()).orElseThrow(() -> new UserNotFoundException(Constants.User.USER_NOT_FOUND));
                String token = redisService.getRefreshToken(user.getEmail(), userSession.getDeviceId());
                if (token == null) {
                    userSession.setActive(false);
                    userSession.setLogoutTime(today);
                    newUserSessionList.add(userSession);
                }
            }
            userSessionRepository.saveAll(newUserSessionList);
        } catch (Exception e) {
            errorLoggingService.log(e);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void autoRemoveRememberMeSessions() {
        try {
            List<UserSession> userSessionList = userSessionRepository.getAllActiveRememberedSessions();
            List<UserSession> newUserSessionList = new ArrayList<>();
            LocalDateTime today = LocalDateTime.now();
            for (UserSession userSession : userSessionList) {
                if (ChronoUnit.DAYS.between(userSession.getLastUpdated(), today) > 7) {
                    userSession.setActive(false);
                    userSession.setLogoutTime(today);
                    newUserSessionList.add(userSession);
                }
            }
            userSessionRepository.saveAll(newUserSessionList);
        } catch (Exception e) {
            errorLoggingService.log(e);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void autoClearInactiveSessions() {
        try {
            List<UserSession> userSessionList = userSessionRepository.getAllInactiveSessions();
            List<UUID> uuidList = userSessionList.stream().map(UserSession::getId).toList();
            userSessionRepository.deleteAllById(uuidList);
        } catch (Exception e) {
            errorLoggingService.log(e);
        }
    }
}

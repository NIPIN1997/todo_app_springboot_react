package com.projectsbynipin.todo_app_backend.service.redis;

import com.projectsbynipin.todo_app_backend.entity.UserSession;
import com.projectsbynipin.todo_app_backend.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AutoLogoutService {

    private final UserSessionRepository userSessionRepository;

    public void autoLogout(String redisKey) {
        UserSession userSession = userSessionRepository.findSessionByRedisKey(redisKey);
        if (userSession != null) {
            userSession.setActive(false);
            userSession.setLogoutTime(LocalDateTime.now());
            userSessionRepository.save(userSession);
        }
    }
}

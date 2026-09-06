package com.projectsbynipin.todo_app_backend.service.redis;

import com.projectsbynipin.todo_app_backend.service.logging.ErrorLoggingService;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ErrorLoggingService errorLoggingService;

    @Value("${security.redis.token-expiration-time}")
    private long redisTokenExpirationTime;

    public void storeRefreshToken(String username, UUID deviceId, String encryptedRefreshToken) {
        String key = Constants.Redis.REDIS_KEY_PREFIX_TOKEN_PREFIX + username + deviceId;
        redisTemplate.opsForValue().set(key, encryptedRefreshToken, Duration.ofMinutes(redisTokenExpirationTime));
    }

    public String getRefreshToken(String username, UUID deviceId) {
        String key = Constants.Redis.REDIS_KEY_PREFIX_TOKEN_PREFIX + username + deviceId;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteRefreshToken(String username, UUID deviceId) {
        String key = Constants.Redis.REDIS_KEY_PREFIX_TOKEN_PREFIX + username + deviceId;
        redisTemplate.delete(key);
    }

    public void addLoginLockoutAttempts(String username) {
        String key = Constants.Redis.REDIS_KEY_PREFIX_LOGIN_LOCKOUT_PREFIX + username;
        Long attemptCount = redisTemplate.opsForValue().increment(key);
        if (attemptCount != null && attemptCount == 1) {
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
        }
    }

    public void clearLoginAttempts(String username) {
        redisTemplate.delete(Constants.Redis.REDIS_KEY_PREFIX_LOGIN_LOCKOUT_PREFIX + username);
    }

    public boolean checkIfUserLockedOut(String username) {
        String key = Constants.Redis.REDIS_KEY_PREFIX_LOGIN_LOCKOUT_PREFIX + username;
        String attemptCount = redisTemplate.opsForValue().get(key);
        return attemptCount != null && Long.parseLong(attemptCount) >= 3;
    }

    public String getRemainingLoginLockoutTime(String username) {
        String key = Constants.Redis.REDIS_KEY_PREFIX_LOGIN_LOCKOUT_PREFIX + username;
        Long time = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (time == null || time <= 0) {
            return "0 seconds";
        }
        long minutes = time / 60;
        long seconds = time % 60;
        if (minutes > 0) {
            return minutes + " minutes : " + seconds + " seconds";
        } else {
            return seconds + " seconds";
        }
    }

    public String getRemainingLoginAttempts(String username) {
        String key = Constants.Redis.REDIS_KEY_PREFIX_LOGIN_LOCKOUT_PREFIX + username;
        String attemptCount = redisTemplate.opsForValue().get(key);
        if (attemptCount != null && Long.parseLong(attemptCount) < 3) {
            return "Your account will be locked after "+(3 - Long.parseLong(attemptCount))+" more failed attempts.";
        }
        return "Your account is locked.";
    }
}

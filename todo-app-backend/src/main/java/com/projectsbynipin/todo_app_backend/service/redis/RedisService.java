package com.projectsbynipin.todo_app_backend.service.redis;

import com.projectsbynipin.todo_app_backend.utility.Constants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeRefreshToken(String username, UUID deviceId, String encryptedRefreshToken) {
        String key = Constants.Redis.REDIS_KEY_PREFIX + username + deviceId;
        redisTemplate.opsForValue().set(key, encryptedRefreshToken, Duration.ofMinutes(10));
    }

    public String getRefreshToken(String username, UUID deviceId) {
        String key = Constants.Redis.REDIS_KEY_PREFIX + username + deviceId;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteRefreshToken(String username, UUID deviceId) {
        String key = Constants.Redis.REDIS_KEY_PREFIX + username + deviceId;
        redisTemplate.delete(key);
    }
}

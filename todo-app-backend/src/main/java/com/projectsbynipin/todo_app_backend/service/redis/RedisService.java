package com.projectsbynipin.todo_app_backend.service.redis;

import com.projectsbynipin.todo_app_backend.utility.Constants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeRefreshToken(String username, String encryptedRefreshToken) {
        String key = Constants.Redis.REDIS_KEY_PREFIX + username;
        redisTemplate.opsForValue().set(key, encryptedRefreshToken, Duration.ofHours(1));
    }

    public String getRefreshToken(String username) {
        String key = Constants.Redis.REDIS_KEY_PREFIX + username;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteRefreshToken(String username) {
        String key = Constants.Redis.REDIS_KEY_PREFIX + username;
        redisTemplate.delete(key);
    }
}

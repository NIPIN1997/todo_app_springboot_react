package com.projectsbynipin.todo_app_backend.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessageListener implements MessageListener {

    private final AutoLogoutService autoLogoutService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = message.toString();
        autoLogoutService.autoLogout(key);
    }
}

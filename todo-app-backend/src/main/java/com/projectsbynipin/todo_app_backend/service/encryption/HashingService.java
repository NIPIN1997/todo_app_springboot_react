package com.projectsbynipin.todo_app_backend.service.encryption;

import com.projectsbynipin.todo_app_backend.exception.LoginFailedException;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import org.springframework.stereotype.Service;
import ua_parser.Client;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

@Service
public class HashingService {

    public String hashDeviceId(Client client, UUID deviceId) {
        try {
            String combine = deviceId.toString() + client.userAgent.family + client.os.family + client.os.major;
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(combine.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new LoginFailedException(Constants.Login.LOGIN_FAILED);
        }
    }
}

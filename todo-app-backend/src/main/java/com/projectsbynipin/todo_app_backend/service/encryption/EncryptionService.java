package com.projectsbynipin.todo_app_backend.service.encryption;

import com.projectsbynipin.todo_app_backend.exception.FailedToDecryptJwtRefreshTokenException;
import com.projectsbynipin.todo_app_backend.exception.FailedToEncryptJwtRefreshTokenException;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    @Value("${security.aes.secret-key}")
    private String aesSecretKey;

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BIT = 128;
    private static final Logger logger = LoggerFactory.getLogger(EncryptionService.class);

    private String encryptToken(String token) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(aesSecretKey);
            SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
            byte[] encryptedToken = cipher.doFinal(token.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[IV_LENGTH + encryptedToken.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(encryptedToken, 0, combined, IV_LENGTH, encryptedToken.length);
            return Base64.getEncoder().encodeToString(combined);

        } catch (Exception e) {
            logger.error("Failed to encrypt JWT refresh token.", e);
            throw new FailedToEncryptJwtRefreshTokenException(Constants.Jwt.FAILED_TO_ENCRYPT_TOKEN);
        }
    }

    private String decryptToken(String encryptedToken) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedToken);
            byte[] decodedKey = Base64.getDecoder().decode(aesSecretKey);
            SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, ALGORITHM);
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            byte[] cipherText = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, IV_LENGTH, cipherText, 0, combined.length - IV_LENGTH);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
            byte[] token = cipher.doFinal(cipherText);
            return new String(token, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Failed to decrypt JWT refresh token.", e);
            throw new FailedToDecryptJwtRefreshTokenException(Constants.Jwt.FAILED_TO_DECRYPT_TOKEN);
        }
    }

    public String getEncryptedToken(String token) {
        return encryptToken(token);
    }

    public String getDecryptedToken(String token) {
        return decryptToken(token);
    }
}

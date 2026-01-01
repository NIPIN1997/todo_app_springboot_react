package com.projectsbynipin.todo_app_backend.service.jwt;

import com.projectsbynipin.todo_app_backend.entity.User;
import com.projectsbynipin.todo_app_backend.entity.UserSession;
import com.projectsbynipin.todo_app_backend.exception.UserNotFoundException;
import com.projectsbynipin.todo_app_backend.repository.UserRepository;
import com.projectsbynipin.todo_app_backend.repository.UserSessionRepository;
import com.projectsbynipin.todo_app_backend.service.encryption.EncryptionService;
import com.projectsbynipin.todo_app_backend.service.redis.RedisService;
import com.projectsbynipin.todo_app_backend.utility.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String jwtSecretKey;
    @Value("${security.jwt.refresh-token-secret-key}")
    private String refreshTokenSecretKey;
    @Value("${security.jwt.remember-me-token-secret-key}")
    private String rememberMeTokenSecretKey;
    @Value("${security.jwt.token-expiration-time}")
    private long jwtTokenExpiration;
    @Value("${security.jwt.refresh-token-expiration-time}")
    private long jwtRefreshTokenExpiration;
    @Value("${security.jwt.remember-me-token-expiration-time}")
    private long jwtRememberMeTokenExpiration;

    private final UserRepository userRepository;
    private final RedisService redisService;
    private final EncryptionService encryptionService;
    private final UserSessionRepository userSessionRepository;

    public JwtService(UserRepository userRepository, RedisService redisService, EncryptionService encryptionService, UserSessionRepository userSessionRepository) {
        this.userRepository = userRepository;
        this.redisService = redisService;
        this.encryptionService = encryptionService;
        this.userSessionRepository = userSessionRepository;
    }

    private Key getSignkey(String secretKey) {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private String createToken(String email, long expirationTime, UUID deviceId, String secretKey) {
        User user = userRepository.findByEmailAndIsDeleted(email, false);
        if (user == null) {
            throw new UserNotFoundException(Constants.User.USER_NOT_FOUND);
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().getName());
        claims.put("id", user.getId());
        claims.put("deviceId", deviceId);
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignkey(secretKey))
                .compact();
    }

    public String generateToken(String email, UUID deviceId) {
        return createToken(email, jwtTokenExpiration, deviceId, jwtSecretKey);
    }

    public String generateRefreshToken(String email, UUID deviceId) {
        return createToken(email, jwtRefreshTokenExpiration, deviceId, refreshTokenSecretKey);
    }

    public String generateRememberMeToken(String email, UUID deviceId) {
        return createToken(email, jwtRememberMeTokenExpiration, deviceId, rememberMeTokenSecretKey);
    }

    private Claims extractAllClaims(String token, String secretKey) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSignkey(secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, String secretKey, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token, secretKey);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token, String secretKey) {
        return extractClaim(token, secretKey, Claims::getSubject);
    }

    public Date extractExpiration(String token, String secretKey) {
        return extractClaim(token, secretKey, Claims::getExpiration);
    }

    public UUID extractDeviceId(String token) {
        return UUID.fromString(extractAllClaims(token, jwtSecretKey).get("deviceId", String.class));
    }

    public boolean isTokenExpired(String token, String secretKey) {
        return extractExpiration(token, secretKey).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token, jwtSecretKey);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, jwtSecretKey));
    }

    public Boolean validateRefreshToken(String token, UUID deviceId) {
        String username = extractUsername(token, refreshTokenSecretKey);
        if (!isTokenExpired(token, refreshTokenSecretKey) && token.equals(encryptionService.getDecryptedToken(redisService.getRefreshToken(username, deviceId)))) {
            redisService.deleteRefreshToken(username, deviceId);
            return true;
        }
        return false;
    }

    public boolean isRememberMeTokenExpired(String token) {
        return extractExpiration(token, rememberMeTokenSecretKey).before(new Date());
    }

    public Boolean checkDeviceActiveForJwt(String token) {
        UUID deviceId = extractDeviceId(token);
        UserSession userSession = userSessionRepository.findByDeviceId(deviceId);
        if (userSession != null) {
            return userSession.isActive();
        }
        return false;
    }
}

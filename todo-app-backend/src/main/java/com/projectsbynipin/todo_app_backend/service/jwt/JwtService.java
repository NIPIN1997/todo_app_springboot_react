package com.projectsbynipin.todo_app_backend.service.jwt;

import com.projectsbynipin.todo_app_backend.entity.User;
import com.projectsbynipin.todo_app_backend.entity.UserSession;
import com.projectsbynipin.todo_app_backend.repository.UserSessionRepository;
import com.projectsbynipin.todo_app_backend.service.encryption.EncryptionService;
import com.projectsbynipin.todo_app_backend.service.redis.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
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

    private final RedisService redisService;
    private final EncryptionService encryptionService;
    private final UserSessionRepository userSessionRepository;

    private Key getSignkey(String secretKey) {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private String createToken(User user, long expirationTime, UUID deviceId, String secretKey) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().getName());
        claims.put("id", user.getId());
        claims.put("deviceId", deviceId);
        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignkey(secretKey))
                .compact();
    }

    public String generateToken(User user, UUID deviceId) {
        return createToken(user, jwtTokenExpiration, deviceId, jwtSecretKey);
    }

    public String generateRefreshToken(User user, UUID deviceId) {
        return createToken(user, jwtRefreshTokenExpiration, deviceId, refreshTokenSecretKey);
    }

    public String generateRememberMeToken(User user, UUID deviceId) {
        return createToken(user, jwtRememberMeTokenExpiration, deviceId, rememberMeTokenSecretKey);
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

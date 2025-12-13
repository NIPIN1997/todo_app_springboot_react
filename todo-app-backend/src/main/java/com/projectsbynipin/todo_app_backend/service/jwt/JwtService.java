package com.projectsbynipin.todo_app_backend.service.jwt;

import com.projectsbynipin.todo_app_backend.entity.User;
import com.projectsbynipin.todo_app_backend.entity.UserSession;
import com.projectsbynipin.todo_app_backend.exception.JwtRefreshTokenExpiredException;
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

    private Key getSignkey() {
        byte[] bytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private String createToken(String email, long expirationTime, UUID deviceId) {
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
                .signWith(getSignkey())
                .compact();
    }

    public String generateToken(String email, UUID deviceId) {
        return createToken(email, jwtTokenExpiration, deviceId);
    }

    public String generateRefreshToken(String email, UUID deviceId) {
        return createToken(email, jwtRefreshTokenExpiration, deviceId);
    }

    public String generateRememberMeToken(String email, UUID deviceId) {
        return createToken(email, jwtRememberMeTokenExpiration, deviceId);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSignkey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public UUID extractDeviceId(String token) {
        return UUID.fromString(extractAllClaims(token).get("deviceId", String.class));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String refreshToken(String token) {
        if (isTokenExpired(token)) {
            throw new JwtRefreshTokenExpiredException(Constants.Jwt.JWT_REFRESH_TOKEN_EXPIRED);
        }
        return generateToken(extractUsername(token), extractDeviceId(token));
    }

    public Boolean validateRefreshToken(String token, UUID deviceId) {
        String username = extractUsername(token);
        if (!isTokenExpired(token) && token.equals(encryptionService.getDecryptedToken(redisService.getRefreshToken(username, deviceId)))) {
            redisService.deleteRefreshToken(username, deviceId);
            return true;
        }
        return false;
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

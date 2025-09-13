package com.projectsbynipin.todo_app_backend.service.jwt;

import com.projectsbynipin.todo_app_backend.entity.User;
import com.projectsbynipin.todo_app_backend.exception.JwtRefreshTokenExpiredException;
import com.projectsbynipin.todo_app_backend.exception.UserNotFoundException;
import com.projectsbynipin.todo_app_backend.repository.UserRepository;
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
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String jwtSecretKey;
    @Value("${security.jwt.token-expiration-time}")
    private long jwtTokenExpiration;
    @Value("${security.jwt.refresh-token-expiration-time}")
    private long jwtRefreshTokenExpiration;

    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Key getSignkey() {
        byte[] bytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private String createToken(String email, long expirationTime) {
        User user = userRepository.findByEmailAndIsDeleted(email, false);
        if (user == null) {
            throw new UserNotFoundException(Constants.USER_NOT_FOUND);
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().getName());
        claims.put("id", user.getId());
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignkey())
                .compact();
    }

    public String generateToken(String email) {
        return createToken(email, jwtTokenExpiration);
    }

    public String generateRefreshToken(String email) {
        return createToken(email, jwtRefreshTokenExpiration);
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

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String refreshToken(String token) {
        if (isTokenExpired(token)) {
            throw new JwtRefreshTokenExpiredException(Constants.JWT_REFRESH_TOKEN_EXPIRED);
        }
        return generateToken(extractUsername(token));
    }
}

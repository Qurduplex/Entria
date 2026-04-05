package edu.pk.qurduplex.identityService.services;


import edu.pk.qurduplex.identityService.config.JwtProperties;
import edu.pk.qurduplex.identityService.dto.JwtTokenDTO;
import edu.pk.qurduplex.identityService.exceptions.JwtAuthenticationException;
import edu.pk.qurduplex.identityService.models.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final Clock clock;

    public JwtTokenDTO generateToken(UUID id, UserRole role) {

        Instant now = Instant.now(clock);
        Instant expiration = now.plus(jwtProperties.getExpiration());

        log.info("Time to add {}", jwtProperties.getExpiration());
        log.info("Now {}",now);
        log.info("Expiration {}", expiration);

        String token = Jwts.builder()
                .subject(id.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim("role", role.name())
                .signWith(getSignInKey())
                .compact();

        return new JwtTokenDTO(token, expiration);
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    public UserRole extractUserRole(String token) {
        Claims claims = extractAllClaims(token);
        String roleName = claims.get("role", String.class);

        if (roleName == null) {
            log.warn("Token missing 'role' claim");
            throw new JwtAuthenticationException("Missing role in token");
        }

        try {
            return UserRole.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            log.error("Invalid role in token: {}", roleName);
            throw new JwtAuthenticationException("Invalid role: " + roleName);
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

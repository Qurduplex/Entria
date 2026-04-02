package edu.pk.qurduplex.identityService.services;


import edu.pk.qurduplex.identityService.config.JwtProperties;
import edu.pk.qurduplex.identityService.dto.JwtTokenDTO;
import edu.pk.qurduplex.identityService.models.UserRole;
import edu.pk.qurduplex.identityService.util.TimeFormatter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final TimeFormatter timeFormatter;

    public JwtTokenDTO generateToken(UUID id, Set<UserRole> roles) {

        Instant now = timeFormatter.now();
        Instant expiration = now.plus(jwtProperties.getExpiration());

        log.info("Time to add {}", jwtProperties.getExpiration());
        log.info("Now {}",now);
        log.info("Expiration {}", expiration);

        String token = Jwts.builder()
                .subject(id.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim("roles", roles)
                .signWith(getSignInKey())
                .compact();

        String formattedExpiration = timeFormatter.formatInstant(expiration);
        return new JwtTokenDTO(token, formattedExpiration);
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    public UserRole extractUserRole(String token) {
        String roleName = extractAllClaims(token).get("role", String.class);
        return UserRole.valueOf(roleName);
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

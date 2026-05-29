package edu.pk.qurduplex.userDataService.services;

import edu.pk.qurduplex.userDataService.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import edu.pk.qurduplex.userDataService.exceptions.JwtAuthenticationException;


@Slf4j
@RequiredArgsConstructor
@Service
public class JwtService {
    private final JwtProperties jwtProperties;

    private SecretKey getSignInKey() {
        byte[] keyBytes = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserId(String token) {
        try{
            return extractAllClaims(token).getSubject();
        } catch (io.jsonwebtoken.ExpiredJwtException exc) {
            throw new JwtAuthenticationException("Token expired");
        } catch (io.jsonwebtoken.JwtException exc) {
            throw new JwtAuthenticationException("Invalid token");
        }
        
    }
}

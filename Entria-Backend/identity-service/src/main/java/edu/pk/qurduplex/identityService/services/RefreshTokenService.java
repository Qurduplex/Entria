package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.identityService.config.JwtProperties;
import edu.pk.qurduplex.identityService.exceptions.RefreshTokenException;
import edu.pk.qurduplex.identityService.models.RefreshToken;
import edu.pk.qurduplex.identityService.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public RefreshToken createRefreshToken(UUID userId) {
        UUID refreshTokenId = UUID.randomUUID();
        Long expirationSeconds = jwtProperties.getRefreshExpiration().getSeconds();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenId)
                .userId(userId)
                .expirationInSeconds(expirationSeconds)
                .build();

        log.info("Creating new refresh token for user id: {}", userId);
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyAndGetToken(UUID requestToken) {
        return refreshTokenRepository.findById(requestToken)
                .orElseThrow(() -> {
                    log.warn("Refresh token is invalid or has expired");
                    return new RefreshTokenException("Refresh token was expired or is invalid. Please make a new signin request.");
                });
    }

    public void deleteRefreshToken(UUID token) {
        refreshTokenRepository.deleteById(token);
    }

    public void deleteRefreshTokenByUserId(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
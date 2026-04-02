package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.identityService.config.JwtProperties;
import edu.pk.qurduplex.identityService.exceptions.RefreshTokenException;
import edu.pk.qurduplex.identityService.models.RefreshToken;
import edu.pk.qurduplex.identityService.repositories.RefreshTokenRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("Should successfully create and save a new refresh token")
    void createRefreshToken_Success() {
        UUID TEST_USER_ID = Instancio.create(UUID.class);
        long EXPIRATION_SECONDS = Instancio.create(Long.class);

        when(jwtProperties.getRefreshExpiration()).thenReturn(Duration.ofSeconds(EXPIRATION_SECONDS));

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken result = refreshTokenService.createRefreshToken(TEST_USER_ID);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isNotNull();
        assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(result.getExpirationInSeconds()).isEqualTo(EXPIRATION_SECONDS);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());

        RefreshToken savedToken = captor.getValue();
        assertThat(savedToken.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(savedToken.getExpirationInSeconds()).isEqualTo(EXPIRATION_SECONDS);
    }

    @Test
    @DisplayName("Should successfully return token if it exists in repository")
    void verifyAndGetToken_Success() {
        UUID TEST_TOKEN_ID = Instancio.create(UUID.class);
        RefreshToken expectedToken = Instancio.create(RefreshToken.class);
        expectedToken.setToken(TEST_TOKEN_ID);

        when(refreshTokenRepository.findById(TEST_TOKEN_ID)).thenReturn(Optional.of(expectedToken));

        RefreshToken result = refreshTokenService.verifyAndGetToken(TEST_TOKEN_ID);

        assertThat(result).isEqualTo(expectedToken);
        verify(refreshTokenRepository).findById(TEST_TOKEN_ID);
    }

    @Test
    @DisplayName("Should throw RefreshTokenException when token does not exist in Redis")
    void verifyAndGetToken_NotFound_ThrowsException() {
        UUID TEST_TOKEN_ID = Instancio.create(UUID.class);

        when(refreshTokenRepository.findById(TEST_TOKEN_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.verifyAndGetToken(TEST_TOKEN_ID))
                .isInstanceOf(RefreshTokenException.class)
                .hasMessageContaining("Refresh token was expired or is invalid");

        verify(refreshTokenRepository).findById(TEST_TOKEN_ID);
    }

    @Test
    @DisplayName("Should call deleteById on repository when deleting token")
    void deleteRefreshToken_Success() {
        UUID TEST_TOKEN_ID = Instancio.create(UUID.class);

        refreshTokenService.deleteRefreshToken(TEST_TOKEN_ID);

        verify(refreshTokenRepository).deleteById(TEST_TOKEN_ID);
    }

    @Test
    @DisplayName("Should call deleteByUserId on repository when logging out from all devices")
    void deleteRefreshTokenByUserId_Success() {
        UUID TEST_USER_ID = Instancio.create(UUID.class);

        refreshTokenService.deleteRefreshTokenByUserId(TEST_USER_ID);

        verify(refreshTokenRepository).deleteByUserId(TEST_USER_ID);
    }
}
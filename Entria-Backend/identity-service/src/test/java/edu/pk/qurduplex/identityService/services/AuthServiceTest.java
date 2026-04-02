package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.identityService.dto.LoginResponseDTO;
import edu.pk.qurduplex.identityService.dto.RegisterResponseDTO;
import edu.pk.qurduplex.identityService.dto.TokenDTO;
import edu.pk.qurduplex.identityService.exceptions.UserAlreadyExistsException;
import edu.pk.qurduplex.identityService.models.AuthCredential;
import edu.pk.qurduplex.identityService.models.UserRole;
import edu.pk.qurduplex.identityService.repositories.AuthRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private VerificationCodeService verificationCodeService;

    @Mock
    private JwtService jwtService;

    @Mock
    private ResetPasswordCodeService resetPasswordCodeService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should successfully register a new user")
    void register_Success() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";
        String TEST_PASSWORD = Instancio.create(String.class);
        String ENCODED_PASSWORD = "hashed_" + TEST_PASSWORD;
        String VERIFICATION_CODE = Instancio.create(String.class);

        when(authRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(verificationCodeService.generateVerificationCode(any())).thenReturn(VERIFICATION_CODE);

        AuthCredential savedCredential = AuthCredential.builder()
                .email(TEST_EMAIL)
                .passwordHash(ENCODED_PASSWORD)
                .build();

        when(authRepository.save(any(AuthCredential.class))).thenReturn(savedCredential);

        RegisterResponseDTO response = authService.register(TEST_EMAIL, TEST_PASSWORD);

        assertThat(response.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(response.isSuccess()).isTrue();

        verify(authRepository).existsByEmail(TEST_EMAIL);
        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(authRepository).save(any(AuthCredential.class));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email is taken")
    void register_EmailAlreadyExists_ThrowsException() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";
        String TEST_PASSWORD = Instancio.create(String.class);

        when(authRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> authService.register(TEST_EMAIL, TEST_PASSWORD))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Email already in use");

        verify(authRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Should request verification code successfully when account is not verified")
    void requestVerificationCode_Success() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";
        UUID TEST_ID = Instancio.create(UUID.class);
        String VERIFICATION_CODE = Instancio.create(String.class);

        AuthCredential credential = AuthCredential.builder()
                .id(TEST_ID)
                .email(TEST_EMAIL)
                .isActive(false)
                .build();

        when(authRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.of(credential));
        when(verificationCodeService.generateVerificationCode(TEST_ID)).thenReturn(VERIFICATION_CODE);

        var response = authService.requestVerificationCode(TEST_EMAIL);

        assertThat(response.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(response.isSuccess()).isTrue();

        verify(authRepository).findByEmail(TEST_EMAIL);
        verify(verificationCodeService).generateVerificationCode(TEST_ID);
    }

    @Test
    @DisplayName("Should throw UserAlreadyVerifiedException when requesting code for already verified account")
    void requestVerificationCode_AlreadyVerified_ThrowsException() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";

        AuthCredential credential = AuthCredential.builder()
                .email(TEST_EMAIL)
                .isActive(true)
                .build();

        when(authRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.of(credential));

        assertThatThrownBy(() -> authService.requestVerificationCode(TEST_EMAIL))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.UserAlreadyVerifiedException.class)
                .hasMessageContaining("Account is already verified");

        verifyNoInteractions(verificationCodeService);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when requesting code for non-existent user")
    void requestVerificationCode_UserNotFound_ThrowsException() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";

        when(authRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> authService.requestVerificationCode(TEST_EMAIL))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.UserNotFoundException.class)
                .hasMessageContaining("not found");

        verifyNoInteractions(verificationCodeService);
    }

    @Test
    @DisplayName("Should successfully verify account")
    void verifyAccount_Success() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";
        String TEST_CODE = Instancio.create(String.class);
        UUID TEST_ID = Instancio.create(UUID.class);

        AuthCredential credential = AuthCredential.builder()
                .id(TEST_ID)
                .email(TEST_EMAIL)
                .isActive(false)
                .build();

        when(authRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.of(credential));
        doNothing().when(verificationCodeService).verifyVerificationCode(TEST_ID, TEST_CODE);

        var response = authService.verifyAccount(TEST_EMAIL, TEST_CODE);

        assertThat(response.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(response.isSuccess()).isTrue();
        assertThat(credential.isActive()).isTrue();

        verify(verificationCodeService).verifyVerificationCode(TEST_ID, TEST_CODE);
        verify(authRepository).save(credential);
    }

    @Test
    @DisplayName("Should throw UserAlreadyVerifiedException when trying to verify already active account")
    void verifyAccount_AlreadyVerified_ThrowsException() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";
        String TEST_CODE = Instancio.create(String.class);

        AuthCredential credential = AuthCredential.builder()
                .email(TEST_EMAIL)
                .isActive(true)
                .build();

        when(authRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.of(credential));

        assertThatThrownBy(() -> authService.verifyAccount(TEST_EMAIL, TEST_CODE))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.UserAlreadyVerifiedException.class)
                .hasMessageContaining("Account is already verified");

        verifyNoInteractions(verificationCodeService);
        verify(authRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when verifying non-existent user")
    void verifyAccount_UserNotFound_ThrowsException() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";
        String TEST_CODE = Instancio.create(String.class);

        when(authRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> authService.verifyAccount(TEST_EMAIL, TEST_CODE))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.UserNotFoundException.class)
                .hasMessageContaining("not found");

        verifyNoInteractions(verificationCodeService);
        verify(authRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully log in and return JWT and Refresh tokens")
    void login_Success() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";
        String TEST_PASSWORD = Instancio.create(String.class);
        String ENCODED_PASSWORD = "hashed_" + TEST_PASSWORD;
        UUID TEST_ID = Instancio.create(UUID.class);
        String GENERATED_JWT = Instancio.create(String.class);
        UUID GENERATED_REFRESH_TOKEN = Instancio.create(UUID.class);
        Set<UserRole> ROLES = Set.of(UserRole.USER);

        AuthCredential credential = AuthCredential.builder()
                .id(TEST_ID)
                .email(TEST_EMAIL)
                .passwordHash(ENCODED_PASSWORD)
                .isActive(true)
                .roles(ROLES)
                .build();

        edu.pk.qurduplex.identityService.dto.JwtTokenDTO jwtTokenDTO =
                new edu.pk.qurduplex.identityService.dto.JwtTokenDTO(GENERATED_JWT, null);

        edu.pk.qurduplex.identityService.models.RefreshToken refreshToken =
                edu.pk.qurduplex.identityService.models.RefreshToken.builder()
                        .token(GENERATED_REFRESH_TOKEN)
                        .build();

        when(authRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.of(credential));
        when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtService.generateToken(TEST_ID, ROLES)).thenReturn(jwtTokenDTO);
        when(refreshTokenService.createRefreshToken(TEST_ID)).thenReturn(refreshToken);

        LoginResponseDTO response = authService.login(TEST_EMAIL, TEST_PASSWORD);

        assertThat(response.getJwtToken()).isEqualTo(GENERATED_JWT);
        assertThat(response.getRefreshToken()).isEqualTo(GENERATED_REFRESH_TOKEN.toString());

        verify(authRepository).findByEmail(TEST_EMAIL);
        verify(passwordEncoder).matches(TEST_PASSWORD, ENCODED_PASSWORD);
        verify(jwtService).generateToken(TEST_ID, ROLES);
        verify(refreshTokenService).createRefreshToken(TEST_ID);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialException when user is not found")
    void login_UserNotFound_ThrowsException() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";
        String TEST_PASSWORD = Instancio.create(String.class);

        when(authRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> authService.login(TEST_EMAIL, TEST_PASSWORD))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.InvalidCredentialException.class)
                .hasMessageContaining("User with email " + TEST_EMAIL + " not found");

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialException when account is not verified")
    void login_AccountNotVerified_ThrowsException() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";
        String TEST_PASSWORD = Instancio.create(String.class);
        String ENCODED_PASSWORD = "hashed_" + TEST_PASSWORD;

        AuthCredential credential = AuthCredential.builder()
                .email(TEST_EMAIL)
                .passwordHash(ENCODED_PASSWORD)
                .isActive(false)
                .build();

        when(authRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.of(credential));

        when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        assertThatThrownBy(() -> authService.login(TEST_EMAIL, TEST_PASSWORD))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.InvalidCredentialException.class)
                .hasMessageContaining("Account with email " + TEST_EMAIL + " is not verified");

        verify(passwordEncoder).matches(TEST_PASSWORD, ENCODED_PASSWORD); // Weryfikujemy, że sprawdzono hasło
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialException when password does not match")
    void login_InvalidPassword_ThrowsException() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";
        String TEST_PASSWORD = Instancio.create(String.class);
        String ENCODED_PASSWORD = "hashed_" + TEST_PASSWORD;

        AuthCredential credential = AuthCredential.builder()
                .email(TEST_EMAIL)
                .passwordHash(ENCODED_PASSWORD)
                .isActive(true)
                .build();

        when(authRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.of(credential));
        when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThatThrownBy(() -> authService.login(TEST_EMAIL, TEST_PASSWORD))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.InvalidCredentialException.class)
                .hasMessageContaining("Invalid email or password");

        verifyNoInteractions(jwtService);
    }


    @Test
    @DisplayName("Should request reset password code successfully")
    void requestResetPasswordCode_Success() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";
        UUID TEST_ID = Instancio.create(UUID.class);
        String RESET_CODE = Instancio.create(String.class);

        AuthCredential credential = AuthCredential.builder()
                .id(TEST_ID)
                .email(TEST_EMAIL)
                .isActive(true)
                .build();

        when(authRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.of(credential));
        when(resetPasswordCodeService.generateResetPasswordCode(TEST_ID)).thenReturn(RESET_CODE);

        var response = authService.requestResetPasswordCode(TEST_EMAIL);

        assertThat(response.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(response.isSuccess()).isTrue();

        verify(authRepository).findByEmail(TEST_EMAIL);
        verify(resetPasswordCodeService).generateResetPasswordCode(TEST_ID);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when requesting reset code for non-existent user")
    void requestResetPasswordCode_UserNotFound_ThrowsException() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";

        when(authRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> authService.requestResetPasswordCode(TEST_EMAIL))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.UserNotFoundException.class)
                .hasMessageContaining("not found");

        verifyNoInteractions(resetPasswordCodeService);
    }

    @Test
    @DisplayName("Should throw UserNotVerifiedException when requesting reset code for unverified account")
    void requestResetPasswordCode_UserNotVerified_ThrowsException() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";

        AuthCredential credential = AuthCredential.builder()
                .email(TEST_EMAIL)
                .isActive(false)
                .build();

        when(authRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.of(credential));

        assertThatThrownBy(() -> authService.requestResetPasswordCode(TEST_EMAIL))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.UserNotVerifiedException.class)
                .hasMessageContaining("is not verified");

        verifyNoInteractions(resetPasswordCodeService);
    }

    @Test
    @DisplayName("Should successfully reset password")
    void resetPassword_Success() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";
        String TEST_CODE = Instancio.create(String.class);
        String NEW_PASSWORD = Instancio.create(String.class);
        String ENCODED_NEW_PASSWORD = "hashed_new_" + NEW_PASSWORD;
        UUID TEST_ID = Instancio.create(UUID.class);

        AuthCredential credential = AuthCredential.builder()
                .id(TEST_ID)
                .email(TEST_EMAIL)
                .isActive(true)
                .passwordHash("old_hash")
                .build();

        when(authRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.of(credential));
        doNothing().when(resetPasswordCodeService).verifyResetPasswordCode(TEST_ID, TEST_CODE);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_NEW_PASSWORD);

        var response = authService.resetPassword(TEST_EMAIL, TEST_CODE, NEW_PASSWORD);

        assertThat(response.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(response.isSuccess()).isTrue();
        assertThat(credential.getPasswordHash()).isEqualTo(ENCODED_NEW_PASSWORD); // Weryfikujemy, czy zmieniono hash

        verify(resetPasswordCodeService).verifyResetPasswordCode(TEST_ID, TEST_CODE);
        verify(passwordEncoder).encode(NEW_PASSWORD);
        verify(authRepository).save(credential);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when resetting password for non-existent user")
    void resetPassword_UserNotFound_ThrowsException() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";
        String TEST_CODE = Instancio.create(String.class);
        String NEW_PASSWORD = Instancio.create(String.class);

        when(authRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> authService.resetPassword(TEST_EMAIL, TEST_CODE, NEW_PASSWORD))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.UserNotFoundException.class)
                .hasMessageContaining("not found");

        verifyNoInteractions(resetPasswordCodeService);
        verifyNoInteractions(passwordEncoder);
        verify(authRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UserNotVerifiedException when resetting password for unverified account")
    void resetPassword_UserNotVerified_ThrowsException() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";
        String TEST_CODE = Instancio.create(String.class);
        String NEW_PASSWORD = Instancio.create(String.class);

        AuthCredential credential = AuthCredential.builder()
                .email(TEST_EMAIL)
                .isActive(false)
                .build();

        when(authRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.of(credential));

        assertThatThrownBy(() -> authService.resetPassword(TEST_EMAIL, TEST_CODE, NEW_PASSWORD))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.UserNotVerifiedException.class)
                .hasMessageContaining("is not verified");

        verifyNoInteractions(resetPasswordCodeService);
        verifyNoInteractions(passwordEncoder);
        verify(authRepository, never()).save(any());
    }

    // --- REFRESH TOKEN TESTS ---

    @Test
    @DisplayName("Should successfully refresh access token")
    void refreshAccessToken_Success() {
        UUID REFRESH_TOKEN_ID = Instancio.create(UUID.class);
        UUID USER_ID = Instancio.create(UUID.class);
        String NEW_JWT = Instancio.create(String.class);
        Set<UserRole> ROLES = Set.of(UserRole.USER);

        edu.pk.qurduplex.identityService.models.RefreshToken validRefreshToken =
                edu.pk.qurduplex.identityService.models.RefreshToken.builder()
                        .token(REFRESH_TOKEN_ID)
                        .userId(USER_ID)
                        .build();

        AuthCredential user = AuthCredential.builder()
                .id(USER_ID)
                .roles(ROLES)
                .build();

        edu.pk.qurduplex.identityService.dto.JwtTokenDTO newJwtDTO =
                new edu.pk.qurduplex.identityService.dto.JwtTokenDTO(NEW_JWT, null);

        when(refreshTokenService.verifyAndGetToken(REFRESH_TOKEN_ID)).thenReturn(validRefreshToken);
        when(authRepository.findById(USER_ID)).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateToken(USER_ID, ROLES)).thenReturn(newJwtDTO);

        TokenDTO response = authService.refreshAccessToken(REFRESH_TOKEN_ID);

        assertThat(response.getJwtToken()).isEqualTo(NEW_JWT);

        verify(refreshTokenService).verifyAndGetToken(REFRESH_TOKEN_ID);
        verify(authRepository).findById(USER_ID);
        verify(jwtService).generateToken(USER_ID, ROLES);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when refreshing token for non-existent user")
    void refreshAccessToken_UserNotFound_ThrowsException() {
        UUID REFRESH_TOKEN_ID = Instancio.create(UUID.class);
        UUID USER_ID = Instancio.create(UUID.class);

        edu.pk.qurduplex.identityService.models.RefreshToken validRefreshToken =
                edu.pk.qurduplex.identityService.models.RefreshToken.builder()
                        .token(REFRESH_TOKEN_ID)
                        .userId(USER_ID)
                        .build();

        when(refreshTokenService.verifyAndGetToken(REFRESH_TOKEN_ID)).thenReturn(validRefreshToken);
        when(authRepository.findById(USER_ID)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> authService.refreshAccessToken(REFRESH_TOKEN_ID))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.UserNotFoundException.class)
                .hasMessageContaining("User associated with token not found");

        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should successfully log out single device")
    void logout_Success() {
        UUID REFRESH_TOKEN_ID = Instancio.create(UUID.class);

        authService.logout(REFRESH_TOKEN_ID);

        verify(refreshTokenService).deleteRefreshToken(REFRESH_TOKEN_ID);
    }

    @Test
    @DisplayName("Should successfully log out from all devices")
    void logoutFromAllDevices_Success() {
        UUID USER_ID = Instancio.create(UUID.class);
        String JWT_TOKEN = Instancio.create(String.class);
        String AUTH_HEADER = "Bearer " + JWT_TOKEN;

        when(jwtService.extractUserId(JWT_TOKEN)).thenReturn(USER_ID.toString());

        authService.logoutFromAllDevices(AUTH_HEADER);

        verify(jwtService).extractUserId(JWT_TOKEN);
        verify(refreshTokenService).deleteRefreshTokenByUserId(USER_ID);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialException when header is invalid during logout from all devices")
    void logoutFromAllDevices_InvalidHeader_ThrowsException() {
        String INVALID_HEADER = "Basic some-token"; // Zły format, nie zaczyna się od Bearer

        assertThatThrownBy(() -> authService.logoutFromAllDevices(INVALID_HEADER))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.InvalidCredentialException.class)
                .hasMessageContaining("Invalid Authorization header format");

        verifyNoInteractions(jwtService);
        verifyNoInteractions(refreshTokenService);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialException when JWT parsing fails during logout from all devices")
    void logoutFromAllDevices_JwtParsingFails_ThrowsException() {
        String JWT_TOKEN = Instancio.create(String.class);
        String AUTH_HEADER = "Bearer " + JWT_TOKEN;

        when(jwtService.extractUserId(JWT_TOKEN)).thenThrow(new RuntimeException("Expired token"));

        assertThatThrownBy(() -> authService.logoutFromAllDevices(AUTH_HEADER))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.InvalidCredentialException.class)
                .hasMessageContaining("Invalid or expired JWT token");

        verify(refreshTokenService, never()).deleteRefreshTokenByUserId(any());
    }
}

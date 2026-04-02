package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.identityService.dto.RegisterResponseDTO;
import edu.pk.qurduplex.identityService.exceptions.UserAlreadyExistsException;
import edu.pk.qurduplex.identityService.models.AuthCredential;
import edu.pk.qurduplex.identityService.repositories.AuthRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @DisplayName("Should request verification code successfully")
    void requestVerificationCode_Success() {
        String TEST_EMAIL = Instancio.create(String.class) + "@example.com";
        UUID TEST_ID = Instancio.create(UUID.class);
        String VERIFICATION_CODE = Instancio.create(String.class);

        AuthCredential credential = AuthCredential.builder()
                .id(TEST_ID)
                .email(TEST_EMAIL)
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
}

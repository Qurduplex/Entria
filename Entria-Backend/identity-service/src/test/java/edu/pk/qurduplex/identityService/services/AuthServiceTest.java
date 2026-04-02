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
        String TEST_EMAIL = Instancio.create(String.class);
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
        String TEST_EMAIL = Instancio.create(String.class);
        String TEST_PASSWORD = Instancio.create(String.class);

        when(authRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> authService.register(TEST_EMAIL, TEST_PASSWORD))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Email already in use");

        verify(authRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }
}

package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.identityService.config.ResetPasswordCodeProperties;
import edu.pk.qurduplex.identityService.models.ResetPasswordCode;
import edu.pk.qurduplex.identityService.repositories.ResetPasswordCodeRepository;
import edu.pk.qurduplex.identityService.util.CodeGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordCodeServiceTest {

    @Mock
    private CodeGenerator codeGenerator;

    @Mock
    private ResetPasswordCodeRepository resetPasswordCodeRepository;

    @Mock
    private ResetPasswordCodeProperties resetPasswordCodeProperties;

    @InjectMocks
    private ResetPasswordCodeService resetPasswordCodeService;

    @BeforeEach
    void setUp() {
        lenient().when(resetPasswordCodeProperties.getCodeLength()).thenReturn(6);
    }

    @Test
    @DisplayName("Should generate and save reset-password code when no prior code exists")
    void generateResetPasswordCode_NoExistingCode_Success() {
        UUID TEST_ID = UUID.randomUUID();
        String GENERATED_CODE = Instancio.create(String.class);
        Long EXPIRATION_TIME = Instancio.create(Long.class);

        when(resetPasswordCodeRepository.existsById(TEST_ID)).thenReturn(false);
        when(codeGenerator.generateCode(6)).thenReturn(GENERATED_CODE);
        when(resetPasswordCodeProperties.getExpirationInSeconds()).thenReturn(EXPIRATION_TIME);

        ResetPasswordCode savedResetCode = ResetPasswordCode.builder()
                .id(TEST_ID)
                .code(GENERATED_CODE)
                .expirationInSeconds(EXPIRATION_TIME)
                .build();

        when(resetPasswordCodeRepository.save(any(ResetPasswordCode.class))).thenReturn(savedResetCode);

        String result = resetPasswordCodeService.generateResetPasswordCode(TEST_ID);

        assertThat(result).isEqualTo(GENERATED_CODE);

        verify(resetPasswordCodeRepository).existsById(TEST_ID);
        verify(resetPasswordCodeRepository, never()).deleteById(any());
        verify(codeGenerator).generateCode(6);
        verify(resetPasswordCodeProperties).getExpirationInSeconds();
        verify(resetPasswordCodeRepository).save(any(ResetPasswordCode.class));
    }

    @Test
    @DisplayName("Should delete existing code before generating and saving a new one")
    void generateResetPasswordCode_ExistingCode_DeletesAndGeneratesNew() {
        UUID TEST_ID = UUID.randomUUID();
        String GENERATED_CODE = Instancio.create(String.class);
        Long EXPIRATION_TIME = Instancio.create(Long.class);

        when(resetPasswordCodeRepository.existsById(TEST_ID)).thenReturn(true);
        when(codeGenerator.generateCode(6)).thenReturn(GENERATED_CODE);
        when(resetPasswordCodeProperties.getExpirationInSeconds()).thenReturn(EXPIRATION_TIME);

        ResetPasswordCode savedResetCode = ResetPasswordCode.builder()
                .id(TEST_ID)
                .code(GENERATED_CODE)
                .expirationInSeconds(EXPIRATION_TIME)
                .build();

        when(resetPasswordCodeRepository.save(any(ResetPasswordCode.class))).thenReturn(savedResetCode);

        String result = resetPasswordCodeService.generateResetPasswordCode(TEST_ID);

        assertThat(result).isEqualTo(GENERATED_CODE);

        verify(resetPasswordCodeRepository).existsById(TEST_ID);
        verify(resetPasswordCodeRepository).deleteById(TEST_ID);
        verify(codeGenerator).generateCode(6);
        verify(resetPasswordCodeProperties).getExpirationInSeconds();
        verify(resetPasswordCodeRepository).save(any(ResetPasswordCode.class));
    }

    @Test
    @DisplayName("Should successfully verify and delete the code when it matches")
    void verifyResetPasswordCode_Success() {
        UUID TEST_ID = Instancio.create(UUID.class);
        String VALID_CODE = Instancio.create(String.class);

        ResetPasswordCode resetCode = ResetPasswordCode.builder()
                .id(TEST_ID)
                .code(VALID_CODE)
                .build();

        when(resetPasswordCodeRepository.findById(TEST_ID)).thenReturn(java.util.Optional.of(resetCode));

        resetPasswordCodeService.verifyResetPasswordCode(TEST_ID, VALID_CODE);

        verify(resetPasswordCodeRepository).findById(TEST_ID);
        verify(resetPasswordCodeRepository).delete(resetCode);
    }

    @Test
    @DisplayName("Should throw VerificationCodeNotFoundException when code does not exist in repository")
    void verifyResetPasswordCode_NotFound_ThrowsException() {
        UUID TEST_ID = Instancio.create(UUID.class);
        String PROVIDED_CODE = Instancio.create(String.class);

        when(resetPasswordCodeRepository.findById(TEST_ID)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> resetPasswordCodeService.verifyResetPasswordCode(TEST_ID, PROVIDED_CODE))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.VerificationCodeNotFoundException.class)
                .hasMessageContaining("Reset-password code not found");

        verify(resetPasswordCodeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw InvalidVerificationCodeException when provided code does not match")
    void verifyResetPasswordCode_InvalidCode_ThrowsException() {
        UUID TEST_ID = Instancio.create(UUID.class);
        String DB_CODE = Instancio.create(String.class);
        String WRONG_CODE = Instancio.create(String.class);

        ResetPasswordCode resetCode = ResetPasswordCode.builder()
                .id(TEST_ID)
                .code(DB_CODE)
                .build();

        when(resetPasswordCodeRepository.findById(TEST_ID)).thenReturn(java.util.Optional.of(resetCode));

        assertThatThrownBy(() -> resetPasswordCodeService.verifyResetPasswordCode(TEST_ID, WRONG_CODE))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.InvalidVerificationCodeException.class)
                .hasMessageContaining("Invalid reset-password code");

        verify(resetPasswordCodeRepository, never()).delete(any());
    }
}
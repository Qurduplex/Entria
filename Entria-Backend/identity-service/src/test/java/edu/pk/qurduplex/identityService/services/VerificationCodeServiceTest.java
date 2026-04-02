package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.identityService.config.VerificationCodeProperties;
import edu.pk.qurduplex.identityService.models.VerificationCode;
import edu.pk.qurduplex.identityService.repositories.VerificationCodeRepository;
import edu.pk.qurduplex.identityService.util.CodeGenerator;
import org.instancio.Instancio;
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
class VerificationCodeServiceTest {

    @Mock
    private CodeGenerator codeGenerator;

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @Mock
    private VerificationCodeProperties verificationCodeProperties;

    @InjectMocks
    private VerificationCodeService verificationCodeService;

    @Test
    @DisplayName("Should generate and save verification code when no prior code exists")
    void generateVerificationCode_NoExistingCode_Success() {
        UUID TEST_ID = UUID.randomUUID();
        String GENERATED_CODE = Instancio.create(String.class);
        Long EXPIRATION_TIME = Instancio.create(Long.class);

        when(verificationCodeRepository.existsById(TEST_ID)).thenReturn(false);
        when(codeGenerator.generateCode(6)).thenReturn(GENERATED_CODE);
        when(verificationCodeProperties.getExpirationInSeconds()).thenReturn(EXPIRATION_TIME);

        VerificationCode savedVerificationCode = VerificationCode.builder()
                .id(TEST_ID)
                .code(GENERATED_CODE)
                .expirationInSeconds(EXPIRATION_TIME)
                .build();

        when(verificationCodeRepository.save(any(VerificationCode.class))).thenReturn(savedVerificationCode);

        String result = verificationCodeService.generateVerificationCode(TEST_ID);

        assertThat(result).isEqualTo(GENERATED_CODE);

        verify(verificationCodeRepository).existsById(TEST_ID);
        verify(verificationCodeRepository, never()).deleteById(any());
        verify(codeGenerator).generateCode(6);
        verify(verificationCodeProperties).getExpirationInSeconds();
        verify(verificationCodeRepository).save(any(VerificationCode.class));
    }

    @Test
    @DisplayName("Should delete existing code before generating and saving a new one")
    void generateVerificationCode_ExistingCode_DeletesAndGeneratesNew() {
        UUID TEST_ID = UUID.randomUUID();
        String GENERATED_CODE = Instancio.create(String.class);
        Long EXPIRATION_TIME = Instancio.create(Long.class);

        when(verificationCodeRepository.existsById(TEST_ID)).thenReturn(true);
        when(codeGenerator.generateCode(6)).thenReturn(GENERATED_CODE);
        when(verificationCodeProperties.getExpirationInSeconds()).thenReturn(EXPIRATION_TIME);

        VerificationCode savedVerificationCode = VerificationCode.builder()
                .id(TEST_ID)
                .code(GENERATED_CODE)
                .expirationInSeconds(EXPIRATION_TIME)
                .build();

        when(verificationCodeRepository.save(any(VerificationCode.class))).thenReturn(savedVerificationCode);

        String result = verificationCodeService.generateVerificationCode(TEST_ID);

        assertThat(result).isEqualTo(GENERATED_CODE);

        verify(verificationCodeRepository).existsById(TEST_ID);
        verify(verificationCodeRepository).deleteById(TEST_ID);
        verify(codeGenerator).generateCode(6);
        verify(verificationCodeProperties).getExpirationInSeconds();
        verify(verificationCodeRepository).save(any(VerificationCode.class));
    }

    @Test
    @DisplayName("Should successfully verify and delete the code when it matches")
    void verifyVerificationCode_Success() {
        UUID TEST_ID = Instancio.create(UUID.class);
        String VALID_CODE = Instancio.create(String.class);

        VerificationCode verificationCode = VerificationCode.builder()
                .id(TEST_ID)
                .code(VALID_CODE)
                .build();

        when(verificationCodeRepository.findById(TEST_ID)).thenReturn(java.util.Optional.of(verificationCode));

        verificationCodeService.verifyVerificationCode(TEST_ID, VALID_CODE);

        verify(verificationCodeRepository).findById(TEST_ID);
        verify(verificationCodeRepository).delete(verificationCode);
    }

    @Test
    @DisplayName("Should throw VerificationCodeNotFoundException when code does not exist in repository")
    void verifyVerificationCode_NotFound_ThrowsException() {
        UUID TEST_ID = Instancio.create(UUID.class);
        String PROVIDED_CODE = Instancio.create(String.class);

        when(verificationCodeRepository.findById(TEST_ID)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> verificationCodeService.verifyVerificationCode(TEST_ID, PROVIDED_CODE))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.VerificationCodeNotFoundException.class)
                .hasMessageContaining("Verification code not found");

        verify(verificationCodeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw InvalidVerificationCodeException when provided code does not match")
    void verifyVerificationCode_InvalidCode_ThrowsException() {
        UUID TEST_ID = Instancio.create(UUID.class);
        String DB_CODE = Instancio.create(String.class);
        String WRONG_CODE = Instancio.create(String.class);

        VerificationCode verificationCode = VerificationCode.builder()
                .id(TEST_ID)
                .code(DB_CODE)
                .build();

        when(verificationCodeRepository.findById(TEST_ID)).thenReturn(java.util.Optional.of(verificationCode));

        assertThatThrownBy(() -> verificationCodeService.verifyVerificationCode(TEST_ID, WRONG_CODE))
                .isInstanceOf(edu.pk.qurduplex.identityService.exceptions.InvalidVerificationCodeException.class)
                .hasMessageContaining("Invalid verification code");

        verify(verificationCodeRepository, never()).delete(any());
    }
}

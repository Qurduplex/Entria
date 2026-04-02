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
}

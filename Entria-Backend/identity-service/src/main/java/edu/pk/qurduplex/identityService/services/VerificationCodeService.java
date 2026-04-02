package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.identityService.config.VerificationCodeProperties;
import edu.pk.qurduplex.identityService.exceptions.InvalidVerificationCodeException;
import edu.pk.qurduplex.identityService.exceptions.VerificationCodeNotFoundException;
import edu.pk.qurduplex.identityService.models.VerificationCode;
import edu.pk.qurduplex.identityService.repositories.VerificationCodeRepository;
import edu.pk.qurduplex.identityService.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class VerificationCodeService {

    private final CodeGenerator codeGenerator;
    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationCodeProperties verificationCodeProperties;

    public String generateVerificationCode(UUID id) {
        if (verificationCodeRepository.existsById(id)) {
            log.info("Verification code already exists for id: {}. Deleting existing code.", id);
            verificationCodeRepository.deleteById(id);
        }

        String code = codeGenerator.generateCode(verificationCodeProperties.getCodeLength());

        VerificationCode verificationCode = VerificationCode.builder()
                .id(id)
                .code(code)
                .expirationInSeconds(verificationCodeProperties.getExpirationInSeconds())
                .build();
        log.info("Generated verification code: {}", verificationCode.toString());

        VerificationCode saved = verificationCodeRepository.save(verificationCode);
        log.info("Saved verification code: {}", saved.toString());

        return saved.getCode();
    }

    public void verifyVerificationCode(UUID id, String verificationCode) {
        VerificationCode code = verificationCodeRepository.findById(id)
                .orElseThrow(() -> new VerificationCodeNotFoundException(
                        "Verification code not found, Check if email is correct or if verification code has expired"));

        if (!code.getCode().equals(verificationCode)) {
            log.warn("Invalid verification code provided for id: {}", id);
            throw new InvalidVerificationCodeException("Invalid verification code");
        }

        log.info("Verification code verified successfully for id: {}", id);
        verificationCodeRepository.delete(code);
    }
}

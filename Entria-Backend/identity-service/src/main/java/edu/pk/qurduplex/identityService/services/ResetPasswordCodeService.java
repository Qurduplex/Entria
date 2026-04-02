package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.identityService.config.ResetPasswordCodeProperties;
import edu.pk.qurduplex.identityService.exceptions.InvalidVerificationCodeException;
import edu.pk.qurduplex.identityService.exceptions.VerificationCodeNotFoundException;
import edu.pk.qurduplex.identityService.models.ResetPasswordCode;
import edu.pk.qurduplex.identityService.repositories.ResetPasswordCodeRepository;
import edu.pk.qurduplex.identityService.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResetPasswordCodeService {

    private final CodeGenerator codeGenerator;
    private final ResetPasswordCodeRepository resetPasswordCodeRepository;
    private final ResetPasswordCodeProperties resetPasswordCodeProperties;

    public String generateResetPasswordCode(UUID id) {
        if (resetPasswordCodeRepository.existsById(id)) {
            log.info("Reset-password code already exists for id: {}. Deleting existing code.", id);
            resetPasswordCodeRepository.deleteById(id);
        }

        String code = codeGenerator.generateCode(resetPasswordCodeProperties.getCodeLength());

        ResetPasswordCode resetPasswordCode = ResetPasswordCode.builder()
                .id(id)
                .code(code)
                .expirationInSeconds(resetPasswordCodeProperties.getExpirationInSeconds())
                .build();
        log.info("Generated reset-password code: {}", resetPasswordCode.toString());

        ResetPasswordCode saved = resetPasswordCodeRepository.save(resetPasswordCode);
        log.info("Saved reset-password code: {}", saved.toString());

        return saved.getCode();
    }

    public void verifyResetPasswordCode(UUID id, String resetPasswordCode) {
        ResetPasswordCode code = resetPasswordCodeRepository.findById(id)
                .orElseThrow(() -> new VerificationCodeNotFoundException(
                        "Reset-password code not found, Check if email is correct or if reset-password code has expired"));

        if (!code.getCode().equals(resetPasswordCode)) {
            log.warn("Invalid reset-password code provided for id: {}", id);
            throw new InvalidVerificationCodeException("Invalid reset-password code");
        }

        log.info("Reset-password code verified successfully for id: {}", id);
        resetPasswordCodeRepository.delete(code);
    }
}

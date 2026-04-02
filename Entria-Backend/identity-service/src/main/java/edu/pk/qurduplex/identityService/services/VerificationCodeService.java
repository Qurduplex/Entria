package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.identityService.config.VerificationCodeProperties;
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

        String code = codeGenerator.generateCode(6);

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
}

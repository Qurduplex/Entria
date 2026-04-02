package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.identityService.dto.AccountVerificationResponseDTO;
import edu.pk.qurduplex.identityService.dto.GenerateVerificationCodeResponseDTO;
import edu.pk.qurduplex.identityService.dto.LoginResponseDTO;
import edu.pk.qurduplex.identityService.dto.RegisterResponseDTO;
import edu.pk.qurduplex.identityService.exceptions.UserAlreadyExistsException;
import edu.pk.qurduplex.identityService.exceptions.UserAlreadyVerifiedException;
import edu.pk.qurduplex.identityService.exceptions.UserNotFoundException;
import edu.pk.qurduplex.identityService.models.AuthCredential;
import edu.pk.qurduplex.identityService.models.UserRole;
import edu.pk.qurduplex.identityService.repositories.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;

    public RegisterResponseDTO register(String email, String password) {
        if (authRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        String hashedPassword = passwordEncoder.encode(password);
        AuthCredential credential = AuthCredential.builder()
                .email(email)
                .passwordHash(hashedPassword)
                .isActive(false)
                .roles(Set.of(UserRole.USER))
                .build();

        log.info("Saving new user with email: {}", email);
        AuthCredential savedCredential = authRepository.save(credential);
        log.info("User with email: {} saved successfully with id: {}", email, savedCredential.getId());

        String verificationCode = verificationCodeService.generateVerificationCode(savedCredential.getId());
        log.info("Generated verification code for user with email: {}: {}", email, verificationCode);

        //todo: send verification email

        return new RegisterResponseDTO(savedCredential.getEmail(), true);
    }

    public GenerateVerificationCodeResponseDTO requestVerificationCode(String email) {
        AuthCredential credential = authRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));

        if (credential.isActive()) {
            throw new UserAlreadyVerifiedException("Account is already verified");
        }

        String verificationCode = verificationCodeService.generateVerificationCode(credential.getId());
        log.info("Generated verification code for user with email: {}: {}", email, verificationCode);
        //todo: send verification email

        return new GenerateVerificationCodeResponseDTO(credential.getEmail(), true);
    }

    public AccountVerificationResponseDTO verifyAccount(String email, String verificationCode) {
        AuthCredential credential = authRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));

        if (credential.isActive()) {
            throw new UserAlreadyVerifiedException("Account is already verified");
        }

        verificationCodeService.verifyVerificationCode(credential.getId(), verificationCode);

        credential.setActive(true);
        authRepository.save(credential);

        log.info("Account with email: {} has been verified successfully", email);

        return new AccountVerificationResponseDTO(credential.getEmail(), true);
    }
}

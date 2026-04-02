package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.identityService.dto.*;
import edu.pk.qurduplex.identityService.exceptions.*;
import edu.pk.qurduplex.identityService.models.AuthCredential;
import edu.pk.qurduplex.identityService.models.UserRole;
import edu.pk.qurduplex.identityService.repositories.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;
    private final JwtService jwtService;
    private final ResetPasswordCodeService resetPasswordCodeService;

    @Transactional
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

    public LoginResponseDTO login(String email, String password) {
        AuthCredential credential = authRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialException("User with email " + email + " not found"));

        if (!passwordEncoder.matches(password, credential.getPasswordHash())) {
            throw new InvalidCredentialException("Invalid email or password");
        }

        if (!credential.isActive()) {
            throw new InvalidCredentialException("Account with email " + email + " is not verified");
        }

        log.info("User with email: {} logged in successfully", email);

        JwtTokenDTO token = jwtService.generateToken(credential.getId(), credential.getRoles());

        log.info("Generated JWT token for user with email: {}", email);

        return new LoginResponseDTO(token.token(), token.expiresAt());
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

    @Transactional
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

    public GenerateResetPasswordCodeResponseDTO requestResetPasswordCode(String email) {
        AuthCredential credential = authRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));

        if (!credential.isActive()) {
            throw new UserNotVerifiedException("Account with email " + email + " is not verified");
        }

        String resetPasswordCode = resetPasswordCodeService.generateResetPasswordCode(credential.getId());
        log.info("Generated reset-password code for user with email: {}: {}", email, resetPasswordCode);
        //todo: send verification email

        return new GenerateResetPasswordCodeResponseDTO(credential.getEmail(), true);
    }

    @Transactional
    public ResetPasswordResponseDTO resetPassword(String email, String resetPasswordCode, String password) {
        AuthCredential credential = authRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));

        if (!credential.isActive()) {
            throw new UserNotVerifiedException("Account with email " + email + " is not verified");
        }

        resetPasswordCodeService.verifyResetPasswordCode(credential.getId(), resetPasswordCode);

        credential.setPasswordHash(passwordEncoder.encode(password));
        authRepository.save(credential);

        log.info("Password for account with email: {} has been reset successfully", email);

        return new ResetPasswordResponseDTO(credential.getEmail(), true);
    }
}

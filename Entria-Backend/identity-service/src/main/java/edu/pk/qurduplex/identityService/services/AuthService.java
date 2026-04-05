package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.identityService.dto.*;
import edu.pk.qurduplex.identityService.exceptions.*;
import edu.pk.qurduplex.identityService.models.AuthCredential;
import edu.pk.qurduplex.identityService.models.RefreshToken;
import edu.pk.qurduplex.identityService.models.UserRole;
import edu.pk.qurduplex.identityService.repositories.AuthRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;
    private final JwtService jwtService;
    private final ResetPasswordCodeService resetPasswordCodeService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public RegisterResponseDTO register(String email, String password, UserRole userRole) {
        if (authRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        String hashedPassword = passwordEncoder.encode(password);
        AuthCredential credential = AuthCredential.builder()
                .email(email)
                .passwordHash(hashedPassword)
                .isActive(false)
                .role(userRole)
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

        JwtTokenDTO jwtToken = jwtService.generateToken(credential.getId(), credential.getRole());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(credential.getId());

        log.info("Generated JWT token for user with email: {}", email);

        return LoginResponseDTO.builder()
                .refreshToken(refreshToken.getToken().toString())
                .jwtToken(jwtToken.token())
                .expiresAt(jwtToken.expiresAt())
                .build();
    }

    @Transactional
    public TokenDTO refreshAccessToken(UUID refreshTokenId) {
        RefreshToken validRefreshToken = refreshTokenService.verifyAndGetToken(refreshTokenId);

        AuthCredential user = authRepository.findById(validRefreshToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User associated with token not found"));

        JwtTokenDTO newAccessToken = jwtService.generateToken(user.getId(), user.getRole());

        log.info("Successfully refreshed access token for user id: {}", user.getId());

        return TokenDTO.builder()
                .jwtToken(newAccessToken.token())
                .expiresAt(newAccessToken.expiresAt())
                .build();
    }

    public void logout(UUID refreshToken) {
        log.info("Received logout request for refresh token: {}", refreshToken);
        refreshTokenService.deleteRefreshToken(refreshToken);
        log.info("Successfully logged out device with refresh token: {}", refreshToken);
    }

    @Transactional
    public void logoutFromAllDevices(String authHeader) {
        if (!authHeader.startsWith("Bearer ")) {
            log.warn("Unauthorized attempt to logout from all devices (invalid header format)");
            throw new InvalidCredentialException("Invalid Authorization header format");
        }

        try {
            String jwtToken = authHeader.substring(7);
            String userIdStr = jwtService.extractUserId(jwtToken);
            UUID userId = UUID.fromString(userIdStr);

            refreshTokenService.deleteRefreshTokenByUserId(userId);

            log.info("Successfully logged out from all devices for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Failed to parse JWT token for logout-all: {}", e.getMessage());
            throw new InvalidCredentialException("Invalid or expired JWT token");
        }
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

package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.identityService.dto.LoginResponseDTO;
import edu.pk.qurduplex.identityService.dto.RegisterResponseDTO;
import edu.pk.qurduplex.identityService.exceptions.UserAlreadyExistsException;
import edu.pk.qurduplex.identityService.models.AuthCredential;
import edu.pk.qurduplex.identityService.models.UserRole;
import edu.pk.qurduplex.identityService.repositories.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

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
}

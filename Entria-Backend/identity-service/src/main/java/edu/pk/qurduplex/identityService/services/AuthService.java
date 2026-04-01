package edu.pk.qurduplex.identityService.services;

import edu.pk.qurduplex.identityService.dto.RegisterResponseDTO;
import edu.pk.qurduplex.identityService.exceptions.UserAlreadyExistsException;
import edu.pk.qurduplex.identityService.models.AuthCredential;
import edu.pk.qurduplex.identityService.models.UserRole;
import edu.pk.qurduplex.identityService.repositories.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

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

        AuthCredential savedCredential = authRepository.save(credential);
        return new RegisterResponseDTO(savedCredential.getEmail(), true);
    }
}

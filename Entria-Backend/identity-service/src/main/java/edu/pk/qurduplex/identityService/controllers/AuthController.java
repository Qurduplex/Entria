package edu.pk.qurduplex.identityService.controllers;

import edu.pk.qurduplex.identityService.dto.RegisterRequestDTO;
import edu.pk.qurduplex.identityService.dto.RegisterResponseDTO;
import edu.pk.qurduplex.identityService.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request
    ) {
        log.info("Received registration request for email: {}", request.getEmail());
        RegisterResponseDTO response = authService.register(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }


}

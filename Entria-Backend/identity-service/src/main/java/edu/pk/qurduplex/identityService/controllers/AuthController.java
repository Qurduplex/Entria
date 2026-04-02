package edu.pk.qurduplex.identityService.controllers;

import edu.pk.qurduplex.identityService.dto.*;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO request
    ) {
        log.info("Received login request for email: {}", request.getEmail());
        LoginResponseDTO response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("verification-code/request")
    public ResponseEntity<GenerateVerificationCodeResponseDTO> requestVerificationCode(
            @RequestBody @Valid GenerateVerificationCodeRequestDTO request) {

        log.info("Received request for verification code for email: {}", request.getEmail());
        GenerateVerificationCodeResponseDTO response = authService.requestVerificationCode(request.getEmail());

        return ResponseEntity.ok(response);
    }

    @PostMapping("verification-code/verify")
    public ResponseEntity<AccountVerificationResponseDTO> verifyCode(
            @RequestBody @Valid AccountVerificationRequestDTO request
    ) {
        log.info("Received account verification request for email: {}", request.getEmail());
        AccountVerificationResponseDTO response = authService.verifyAccount(request.getEmail(), request.getVerificationCode());
        return ResponseEntity.ok(response);
    }

    @PostMapping("reset-password-code/request")
    public ResponseEntity<GenerateResetPasswordCodeResponseDTO> requestResetPassword(
            @RequestBody @Valid GenerateResetPasswordCodeRequestDTO request) {

        log.info("Received request for reset password code for email: {}", request.getEmail());
        GenerateResetPasswordCodeResponseDTO response = authService.requestResetPasswordCode(request.getEmail());

        return ResponseEntity.ok(response);
    }

//    @PostMapping("verification-code/verify")
//    public ResponseEntity<AccountVerificationResponseDTO> verifyCode(
//            @RequestBody @Valid AccountVerificationRequestDTO request
//    ) {
//        log.info("Received account verification request for email: {}", request.getEmail());
//        AccountVerificationResponseDTO response = authService.verifyAccount(request.getEmail(), request.getVerificationCode());
//        return ResponseEntity.ok(response);
//    }









}

package edu.pk.qurduplex.identityService.controllers;

import edu.pk.qurduplex.identityService.dto.*;
import edu.pk.qurduplex.identityService.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration, login and session management")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account using email and password."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid registration payload or malformed JSON", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request
    ) {
        log.info("Received registration request for email: {}", request.getEmail());
        RegisterResponseDTO response = authService.register(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user with email and password and returns access/refresh tokens."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid login payload or malformed JSON", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials or account not verified", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO request
    ) {
        log.info("Received login request for email: {}", request.getEmail());
        LoginResponseDTO response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token using a valid refresh token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token payload or malformed JSON", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "403", description = "Refresh token is invalid or expired", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "404", description = "User associated with token not found", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenDTO> refreshToken(
            @RequestBody @Valid RefreshTokenRequestDTO request
    ) {
        log.info("Received refresh token request for token: {}", request.getRefreshToken());
        TokenDTO response = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Logout from current session",
            description = "Invalidates the provided refresh token and logs the user out from the current device/session."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged out successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid logout payload or malformed JSON", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody @Valid RefreshTokenRequestDTO request) {
        log.info("Received logout request for token: {}", request.getRefreshToken());
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Logout from all devices",
            description = "Invalidates all active sessions/tokens for the currently authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged out from all devices successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Authorization header/token", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/logout/all")
    public ResponseEntity<Void> logoutFromAllDevices(
            @RequestHeader("Authorization") String authHeader
    ) {
        log.info("Received logout all devices request");
        authService.logoutFromAllDevices(authHeader);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Request account verification code",
            description = "Sends or generates a verification code for the provided email address."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification code generated/sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or malformed JSON", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "409", description = "Account is already verified", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/verification-code/request")
    public ResponseEntity<GenerateVerificationCodeResponseDTO> requestVerificationCode(
            @RequestBody @Valid GenerateVerificationCodeRequestDTO request) {

        log.info("Received request for verification code for email: {}", request.getEmail());
        GenerateVerificationCodeResponseDTO response = authService.requestVerificationCode(request.getEmail());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Verify account",
            description = "Verifies a user account using email and verification code."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid verification payload, malformed JSON or invalid verification code", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "404", description = "User not found or verification code not found/expired", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "409", description = "Account is already verified", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/verification-code/verify")
    public ResponseEntity<AccountVerificationResponseDTO> verifyCode(
            @RequestBody @Valid AccountVerificationRequestDTO request
    ) {
        log.info("Received account verification request for email: {}", request.getEmail());
        AccountVerificationResponseDTO response = authService.verifyAccount(request.getEmail(), request.getVerificationCode());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Request reset password code",
            description = "Sends or generates a password reset code for the provided email address."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reset password code generated/sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or malformed JSON", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "403", description = "Account is not verified", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/reset-password-code/request")
    public ResponseEntity<GenerateResetPasswordCodeResponseDTO> requestResetPassword(
            @RequestBody @Valid GenerateResetPasswordCodeRequestDTO request) {

        log.info("Received request for reset password code for email: {}", request.getEmail());
        GenerateResetPasswordCodeResponseDTO response = authService.requestResetPasswordCode(request.getEmail());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Reset password",
            description = "Resets the user password using email, reset code and new password."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid reset payload, malformed JSON or invalid reset code", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "403", description = "Account is not verified", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "404", description = "User not found or reset-password code not found/expired", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/reset-password-code/reset")
    public ResponseEntity<ResetPasswordResponseDTO> resetPassword(
            @RequestBody @Valid ResetPasswordRequestDTO request
    ) {
        log.info("Received account verification request for email: {}", request.getEmail());
        ResetPasswordResponseDTO response = authService.resetPassword(
                request.getEmail(),
                request.getResetPasswordCode(),
                request.getPassword());
        return ResponseEntity.ok(response);
    }
}

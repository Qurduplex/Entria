package edu.pk.qurduplex.identityService.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // MethodArgumentNotValidException is thrown when @Valid validation fails on a request body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("Failed to parse incoming HTTP message: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorMap("Malformed JSON request or invalid data format (e.g., invalid UUID)"));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, String>> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.warn("Missing required header: {}", ex.getHeaderName());

        // If the missing header is "Authorization", we return 401 Unauthorized instead of 400 Bad Request
        if ("Authorization".equalsIgnoreCase(ex.getHeaderName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorMap("Missing Authorization header"));
        }

        // For other missing headers, we return 400 Bad Request
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorMap(ex.getMessage()));
    }

    // --- Authentication & Identity Errors ---
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        log.warn("Registration failed - User already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorMap(ex.getMessage())); // 409 Conflict
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentialException(InvalidCredentialException ex) {
        log.warn("Authentication failed - Invalid credentials: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorMap(ex.getMessage())); // 401 Unauthorized
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        log.warn("Operation failed - User not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorMap(ex.getMessage())); // 404 Not Found
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<Map<String, String>> handleUserNotVerifiedException(UserNotVerifiedException ex) {
        log.warn("Access denied - User not verified: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorMap(ex.getMessage())); // 403 Forbidden
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyVerifiedException(UserAlreadyVerifiedException ex) {
        log.warn("Verification failed - User already verified: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorMap(ex.getMessage())); // 409 Conflict (lub 400 Bad Request)
    }

    // --- Verification Code Errors ---

    @ExceptionHandler(VerificationCodeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleVerificationCodeNotFoundException(VerificationCodeNotFoundException ex) {
        log.warn("Verification code not found or expired: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorMap(ex.getMessage())); // 404 Not Found
    }


    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidVerificationCodeException(InvalidVerificationCodeException ex) {
        log.warn("Invalid verification code provided: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorMap(ex.getMessage())); // 400 Bad Request
    }

    // --- Reset Password Code Errors ---

    @ExceptionHandler(ResetPasswordCodeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResetPasswordCodeNotFoundException(ResetPasswordCodeNotFoundException ex) {
        log.warn("Reset password code not found or expired: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorMap(ex.getMessage())); // 404 Not Found
    }

    @ExceptionHandler(InvalidResetPasswordCodeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidResetPasswordCodeException(InvalidResetPasswordCodeException ex) {
        log.warn("Invalid reset-password code provided: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorMap(ex.getMessage())); // 400 Bad Request
    }

    // --- Refresh Token Errors ---

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<Map<String, String>> handleRefreshTokenException(RefreshTokenException ex) {
        log.warn("Token refresh failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorMap(ex.getMessage())); // 403 Forbidden
    }

    // --- Helper Method ---

    private Map<String, String> createErrorMap(String message) {
        Map<String, String> map = new HashMap<>();
        map.put("message", message);
        return map;
    }
}
package edu.pk.qurduplex.identityService.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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

    // --- Authentication & Identity Errors ---
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorMap(ex.getMessage())); // 409 Conflict
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentialException(InvalidCredentialException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorMap(ex.getMessage())); // 401 Unauthorized
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorMap(ex.getMessage())); // 404 Not Found
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<Map<String, String>> handleUserNotVerifiedException(UserNotVerifiedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorMap(ex.getMessage())); // 403 Forbidden
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyVerifiedException(UserAlreadyVerifiedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorMap(ex.getMessage())); // 409 Conflict (lub 400 Bad Request)
    }

    // --- Verification Code Errors ---

    @ExceptionHandler(VerificationCodeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleVerificationCodeNotFoundException(VerificationCodeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorMap(ex.getMessage())); // 404 Not Found
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidVerificationCodeException(InvalidVerificationCodeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorMap(ex.getMessage())); // 400 Bad Request
    }

    // --- Helper Method ---

    private Map<String, String> createErrorMap(String message) {
        Map<String, String> map = new HashMap<>();
        map.put("message", message);
        return map;
    }
}
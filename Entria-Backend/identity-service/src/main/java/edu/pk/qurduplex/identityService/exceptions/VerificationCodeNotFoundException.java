package edu.pk.qurduplex.identityService.exceptions;

public class VerificationCodeNotFoundException extends RuntimeException {
    public VerificationCodeNotFoundException(String message) {
        super(message);
    }
}

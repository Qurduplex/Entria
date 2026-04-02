package edu.pk.qurduplex.identityService.exceptions;

public class ResetPasswordCodeNotFoundException extends RuntimeException {
    public ResetPasswordCodeNotFoundException(String message) {
        super(message);
    }
}

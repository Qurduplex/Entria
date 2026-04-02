package edu.pk.qurduplex.identityService.exceptions;

public class InvalidResetPasswordCodeException extends RuntimeException {
    public InvalidResetPasswordCodeException(String message) {
        super(message);
    }
}

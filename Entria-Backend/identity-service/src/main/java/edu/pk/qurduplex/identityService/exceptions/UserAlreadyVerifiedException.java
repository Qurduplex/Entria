package edu.pk.qurduplex.identityService.exceptions;

public class UserAlreadyVerifiedException extends RuntimeException {
    public UserAlreadyVerifiedException(String message) {
        super(message);
    }
}

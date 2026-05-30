package edu.pk.qurduplex.appRegistryService.exceptions;

public class ApplicationNameTakenException extends RuntimeException {
    public ApplicationNameTakenException(String message) {
        super(message);
    }
}

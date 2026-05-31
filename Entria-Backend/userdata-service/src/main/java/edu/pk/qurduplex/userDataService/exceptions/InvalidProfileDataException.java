package edu.pk.qurduplex.userDataService.exceptions;

public class InvalidProfileDataException extends RuntimeException {
    public InvalidProfileDataException(String message){
        super(message);
    }
}

package edu.pk.qurduplex.userDataService.exceptions;

import lombok.NoArgsConstructor;


public class UserProfileNotFoundException extends RuntimeException {
    public UserProfileNotFoundException(String message) {
        super(message);
    }
}

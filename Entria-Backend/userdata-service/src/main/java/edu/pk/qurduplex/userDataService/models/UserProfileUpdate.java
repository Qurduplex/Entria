package edu.pk.qurduplex.userDataService.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserProfileUpdate {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String pesel;
    private String sex;
    private String birthDate;
}

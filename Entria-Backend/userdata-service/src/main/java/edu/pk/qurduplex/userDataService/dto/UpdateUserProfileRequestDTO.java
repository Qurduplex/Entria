package edu.pk.qurduplex.userDataService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequestDTO {
    @Size(max=50)
    private String firstName;
    @Size(max=50)
    private String lastName;
    @Pattern(regexp = "^\\d{9}$", message = "phone number must be 9 digits long")
    private String phoneNumber;
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "birth date must be in format YYYY-MM-DD")
    private String birthDate;
    @Pattern(regexp = "^\\d{11}$", message = "pesel must be 11 digits long")
    private String pesel;
    @Pattern(regexp = "^[MF]$", message = "sex must be M or F")
    private String sex;
    private MultipartFile profilePicture;
}

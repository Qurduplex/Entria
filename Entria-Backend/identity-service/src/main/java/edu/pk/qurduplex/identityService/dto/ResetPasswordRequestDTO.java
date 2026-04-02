package edu.pk.qurduplex.identityService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDTO {

    @NotBlank(message = "email is required")
    @Email(message = "email should be valid")
    private String email;

    @NotBlank(message = "verification code is required")
    private String resetPasswordCode;

    @NotBlank(message = "password is required")
    @Size(min = 8, message = "password at least 8 characters long")
    private String password;
}

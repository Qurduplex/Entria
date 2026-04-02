package edu.pk.qurduplex.identityService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountVerificationRequestDTO {

    @NotBlank(message = "email is required")
    @Email(message = "email should be valid")
    private String email;

    @NotBlank(message = "verification code is required")
    private String verificationCode;
}

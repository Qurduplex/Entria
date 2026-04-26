package edu.pk.qurduplex.identityService.dto;

import edu.pk.qurduplex.identityService.models.UserRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    @NotBlank(message = "email is required")
    @Email(message = "email should be valid")
    private String email;

    @NotBlank(message = "first name is required")
    @Size(min = 2, max = 50, message = "first name must be between 2 and 50 characters long")
    @Pattern(
            regexp = "^[\\p{L}\\s\\-']+$",
            message = "first name can only contain letters, spaces, hyphens, and apostrophes"
    )
    private String firstName;

    @NotBlank(message = "last name is required")
    @Size(min = 2, max = 50, message = "last name must be between 2 and 50 characters long")
    @Pattern(
            regexp = "^[\\p{L}\\s\\-']+$",
            message = "last name can only contain letters, spaces, hyphens, and apostrophes"
    )
    private String lastName;

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 32, message = "password must be between 8 and 32 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "password must contain at least one uppercase letter, one number and one special character"
    )
    private String password;

    @NotNull(message = "user role is required")
    private UserRole userRole;

    @AssertTrue(message = "Terms and conditions must be accepted")
    private boolean termsAccepted;
}

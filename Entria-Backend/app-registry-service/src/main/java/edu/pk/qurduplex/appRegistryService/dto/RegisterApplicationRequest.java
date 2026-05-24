package edu.pk.qurduplex.appRegistryService.dto;

import edu.pk.qurduplex.common.models.OAuthPermission;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterApplicationRequest {
    @NotBlank(message = "Application name is required")
    @Size(min = 3, max = 30, message = "Application name must be between 3 and 30 characters long")
    private String name;

    private MultipartFile logo;

    private MultipartFile tosPdf;

    @NotEmpty(message = "Permissions map cannot be empty")
    @NotNull(message = "Permissions map is required")
    private Map<OAuthPermission, @NotNull(message = "Permission requirement status (true/false) must be specified") Boolean> permissions;

    private String redirectUri;
}

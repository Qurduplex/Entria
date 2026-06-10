package edu.pk.qurduplex.appRegistryService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class RegenerateAuthorizeUrlRequest {
    @NotNull(message = "App ID is required")
    private UUID appId;
}

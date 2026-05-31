package edu.pk.qurduplex.appRegistryService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class UpdateApplicationRequest {
    @NotNull(message = "App ID is required")
    private UUID appId;
    private String name;
    private String domain;
    private String description;
    private String redirectUri;
}
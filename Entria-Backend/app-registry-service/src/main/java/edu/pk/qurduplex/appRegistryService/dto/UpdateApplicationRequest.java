package edu.pk.qurduplex.appRegistryService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import edu.pk.qurduplex.common.models.OAuthPermission;

@Data
public class UpdateApplicationRequest {
    @NotNull(message = "App ID is required")
    private UUID appId;
    private String name;
    private String redirectUri;

    private MultipartFile logo;
    private MultipartFile tosPdf;
    private Map<OAuthPermission, Boolean> permissions;

}
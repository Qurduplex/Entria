package edu.pk.qurduplex.appRegistryService.dto;

import edu.pk.qurduplex.common.models.OAuthPermission;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ApplicationDetails {
    private UUID id;
    private String clientId;
    private String name;
    private Map<OAuthPermission, Boolean> permissions;
    private String redirectUri;
    private String authorizeUrl;
    private String logoUrl;
    private String tosPdfUrl;
    private UUID developerId;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

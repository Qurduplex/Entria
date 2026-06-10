package edu.pk.qurduplex.appRegistryService.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegenerateAuthorizeUrlResponse {
    private UUID appId;
    private String clientId;
    private String authorizeUrl;
    
}

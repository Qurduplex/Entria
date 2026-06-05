package edu.pk.qurduplex.appRegistryService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterApplicationResponse {
    private String clientId;
    private String clientSecret;
    private String name;
    private String redirectUri;
    private String authorizeUrl;
    private String logoUrl;
}

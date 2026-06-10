package edu.pk.qurduplex.appRegistryService.dto;

import lombok.Data;
import java.util.UUID;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegenerateClientSecretResponse {
    private UUID appId;
    private String clientSecret;
    private String clientId;
}

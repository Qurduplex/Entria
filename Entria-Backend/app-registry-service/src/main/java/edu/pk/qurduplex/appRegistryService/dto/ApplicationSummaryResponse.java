package edu.pk.qurduplex.appRegistryService.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class ApplicationSummaryResponse {
    private UUID appId;
    private String name;
    private String logoUrl;
    private boolean active;
}
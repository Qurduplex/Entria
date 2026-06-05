package edu.pk.qurduplex.appRegistryService.mappers;

import edu.pk.qurduplex.appRegistryService.dto.ApplicationDetails;
import edu.pk.qurduplex.appRegistryService.models.DeveloperApplication;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.stream.Collectors;

public class AppMapper {
    public static ApplicationDetails toApplicationDetails(DeveloperApplication app, String authorizeUrl) {
        return ApplicationDetails.builder()
                .id(app.getId())
                .clientId(app.getClientId())
                .name(app.getName())
                .permissions(app.getPermissions())
                .redirectUri(app.getRedirectUri())
                .logoUrl(app.getLogoUrl())
                .tosPdfUrl(app.getTosPdfUrl())
                .developerId(app.getDeveloperId())
                .active(app.isActive())
                .createdAt(app.getCreatedAt())
                .updatedAt(app.getUpdatedAt())
                .authorizeUrl(authorizeUrl)
                .build();
    }
}

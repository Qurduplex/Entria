package edu.pk.qurduplex.appRegistryService.utils;

import edu.pk.qurduplex.appRegistryService.config.OauthProperties;
import edu.pk.qurduplex.appRegistryService.models.DeveloperApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OAuthLinkGenerator {

    private final OauthProperties oauthProperties;

    public String generateLink(DeveloperApplication app) {
        String appScopes = app.getPermissions().keySet().stream()
                .map(permission -> permission.name().toLowerCase())
                .collect(Collectors.joining(" "));

        String finalScopes = "openid";
        if (!appScopes.isEmpty()) {
            finalScopes += " " + appScopes;
        }

        return UriComponentsBuilder.fromUriString(oauthProperties.getAuthorizeUrl())
                .queryParam("client_id", app.getClientId())
                .queryParam("redirect_uri", app.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", finalScopes)
                .toUriString();
    }
}
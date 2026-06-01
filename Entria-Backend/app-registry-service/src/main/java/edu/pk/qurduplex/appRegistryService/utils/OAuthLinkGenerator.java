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

    public String generateAuthorizeUrl(DeveloperApplication app) {
        if (app.getClientId() == null || app.getRedirectUri() == null) {
            return null;
        }

        return UriComponentsBuilder.fromHttpUrl(oauthProperties.getAuthorizeUrl())
                .queryParam("client_id", app.getClientId())
                .queryParam("redirect_uri", app.getRedirectUri())
                .queryParam("response_type", "code")
                .toUriString();
    }
}
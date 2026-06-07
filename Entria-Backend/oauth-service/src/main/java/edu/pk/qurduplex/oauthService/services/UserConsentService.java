package edu.pk.qurduplex.oauthService.services;

import edu.pk.qurduplex.oauthService.dto.UserAuthorizedAppDTO;
import edu.pk.qurduplex.oauthService.models.AuthorizationConsent;
import edu.pk.qurduplex.oauthService.repositories.AuthorizationConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserConsentService {

    private final AuthorizationConsentRepository consentRepository;
    private final RegisteredClientRepository registeredClientRepository;
    private final AppGrpcIntegrationService appGrpcIntegrationService;

    public List<UserAuthorizedAppDTO> getUserConsents(String principalName) {
        List<AuthorizationConsent> consents = consentRepository.findByPrincipalName(principalName);

        return consents.stream().map(consent -> {
            RegisteredClient registeredClient = registeredClientRepository.findById(consent.getRegisteredClientId());
            String clientId = (registeredClient != null) ? registeredClient.getClientId() : "Unknown";

            AppGrpcIntegrationService.AppExternalDetails appDetails = appGrpcIntegrationService.getAppDetails(clientId);

            List<String> authorities = Arrays.stream(consent.getAuthorities().split(","))
                    .map(String::trim)
                    .filter(auth -> !auth.isEmpty())
                    .collect(Collectors.toList());

            return UserAuthorizedAppDTO.builder()
                    .clientId(clientId)
                    .appName(appDetails.appName())
                    .appLogoUrl(appDetails.appLogoUrl())
                    .redirectUri(appDetails.redirectUri())
                    .grantedAuthorities(authorities)
                    .build();

        }).collect(Collectors.toList());
    }

    @Transactional
    public void revokeConsent(String principalName, String clientId) {
        RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientId);

        if (registeredClient != null) {
            consentRepository.deleteByRegisteredClientIdAndPrincipalName(
                    registeredClient.getId(),
                    principalName
            );
        }
    }
}
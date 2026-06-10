package edu.pk.qurduplex.appRegistryService.services;

import edu.pk.qurduplex.appRegistryService.config.OauthProperties;
import edu.pk.qurduplex.appRegistryService.dto.ApplicationDetails;
import edu.pk.qurduplex.appRegistryService.dto.ApplicationSummaryResponse;
import edu.pk.qurduplex.appRegistryService.dto.RegisterApplicationResponse;
import edu.pk.qurduplex.appRegistryService.dto.RegenerateClientSecretResponse;
import edu.pk.qurduplex.appRegistryService.dto.RegenerateAuthorizeUrlResponse;
import edu.pk.qurduplex.appRegistryService.exceptions.ApplicationNameTakenException;
import edu.pk.qurduplex.appRegistryService.exceptions.ApplicationNotFoundException;
import edu.pk.qurduplex.appRegistryService.exceptions.ForbiddenAccessException;
import edu.pk.qurduplex.appRegistryService.mappers.AppMapper;
import edu.pk.qurduplex.appRegistryService.models.DeveloperApplication;
import edu.pk.qurduplex.appRegistryService.repositories.DeveloperApplicationRepository;
import edu.pk.qurduplex.appRegistryService.utils.OAuthLinkGenerator;
import edu.pk.qurduplex.common.models.OAuthPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppRegistryService {

    private final DeveloperApplicationRepository applicationRepository;
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;
    private final OAuthLinkGenerator oAuthLinkGenerator;

    @Transactional
    public RegisterApplicationResponse registerApplication(
            UUID developerId,
            String name,
            MultipartFile logo,
            MultipartFile tosPdf,
            Map<OAuthPermission, Boolean> permissions,
            String redirectUri){

        if (applicationRepository.existsByNameIgnoreCase(name)) {
            throw new ApplicationNameTakenException(
                    String.format("Application with name '%s' already exists. Please choose a different name.", name)
            );
        }

        String logoUrl = null;
        if (logo != null && !logo.isEmpty()) {
            logoUrl = fileStorageService.uploadFile(logo, "logos");
        }

        String tosUrl = null;
        if (tosPdf != null && !tosPdf.isEmpty()) {
            tosUrl = fileStorageService.uploadFile(tosPdf, "tos");
        }

        String clientId = UUID.randomUUID().toString();
        String plainClientSecret = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        String hashedSecret = passwordEncoder.encode(plainClientSecret);

        DeveloperApplication application = DeveloperApplication.builder()
                .developerId(developerId)
                .clientId(clientId)
                .clientSecretHash(hashedSecret)
                .name(name)
                .redirectUri(redirectUri)
                .permissions(permissions)
                .logoUrl(logoUrl)
                .tosPdfUrl(tosUrl)
                .createdAt(LocalDateTime.now())
                .build();

        DeveloperApplication savedApplication = applicationRepository.save(application);

        String authUrl = oAuthLinkGenerator.generateLink(savedApplication);

        return RegisterApplicationResponse.builder()
                .clientId(savedApplication.getClientId())
                .clientSecret(plainClientSecret)
                .name(savedApplication.getName())
                .redirectUri(redirectUri)
                .authorizeUrl(authUrl)
                .logoUrl(savedApplication.getLogoUrl())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ApplicationSummaryResponse> getDeveloperApplications(UUID developerId) {
        return applicationRepository.findAllByDeveloperId(developerId).stream()
                .map(app -> ApplicationSummaryResponse.builder()
                        .appId(app.getId())
                        .name(app.getName())
                        .logoUrl(app.getLogoUrl())
                        .active(app.isActive())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ApplicationDetails getApplicationDetails(UUID developerId, UUID appId) {
        DeveloperApplication app = getAndVerifyOwnership(appId, developerId);

        String authUrl = oAuthLinkGenerator.generateLink(app);

        return AppMapper.toApplicationDetails(app, authUrl);
    }

    @Transactional
    public void deactivateApplication(UUID appId, UUID developerId) {
        DeveloperApplication app = getAndVerifyOwnership(appId, developerId);

        if (!app.isActive()) {
            throw new IllegalStateException("Application is already deactivated.");
        }

        app.setActive(false);
        applicationRepository.save(app);
    }

    @Transactional
    public void deleteApplication(UUID appId, UUID developerId) {
        DeveloperApplication app = getAndVerifyOwnership(appId, developerId);

        applicationRepository.delete(app);
    }

    private DeveloperApplication getAndVerifyOwnership(UUID appId, UUID developerId) {
        DeveloperApplication app = applicationRepository.findById(appId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        String.format("Application with ID '%s' not found.", appId)));

        if (!app.getDeveloperId().equals(developerId)) {
            throw new ForbiddenAccessException("You do not have permission to access this application.");
        }

        return app;
    }

    @Transactional
    public ApplicationDetails updateApplication(
        UUID developerId,
        UUID appId,
        String name,
        String redirectUri,
        MultipartFile logo,
        MultipartFile tosPdf,
        Map<OAuthPermission, Boolean> permissions
    ) {
        DeveloperApplication app = getAndVerifyOwnership(appId, developerId);

        boolean hasChanges =
            (name != null && !name.isBlank()) ||
            (redirectUri != null && !redirectUri.isBlank()) ||
            (logo != null && !logo.isEmpty()) ||
            (tosPdf != null && !tosPdf.isEmpty()) ||
            (permissions != null && !permissions.isEmpty());

        if (!hasChanges) {
            throw new IllegalArgumentException("At least one field to update must be provided.");
        }

        if (name != null && !name.isEmpty() && applicationRepository.existsByNameIgnoreCaseAndIdNot(name, appId)) {
            throw new ApplicationNameTakenException(
                    String.format("Application with name '%s' already exists. Please choose a different name.", name)
            );
        }

        if (logo != null && !logo.isEmpty()) {
            app.setLogoUrl(fileStorageService.uploadFile(logo, "logos"));
        }

        if (tosPdf != null && !tosPdf.isEmpty()) {
            app.setTosPdfUrl(fileStorageService.uploadFile(tosPdf, "tos"));
        }

        if (permissions != null && !permissions.isEmpty()) {
            app.setPermissions(permissions);
        }

        if (name != null && !name.isBlank()){
            if (applicationRepository.existsByNameIgnoreCaseAndIdNot(name, appId)) {
                throw new ApplicationNameTakenException(
                        String.format("Application with name '%s' already exists. Please choose a different name.", name)
                );
            }
            app.setName(name);
        }

        if (redirectUri != null && !redirectUri.isBlank()) {
            app.setRedirectUri(redirectUri);
        }

        app.setUpdatedAt(LocalDateTime.now());

        DeveloperApplication updatedApp = applicationRepository.save(app);

        return AppMapper.toApplicationDetails(updatedApp, oAuthLinkGenerator.generateLink(updatedApp));
    }

    private String generatePlainClientSecret() {
        return UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
    }

    @Transactional
    public RegenerateClientSecretResponse regenerateClientSecret(
        UUID developerId,
        UUID appId
    ) {
        DeveloperApplication app = getAndVerifyOwnership(appId, developerId);


        if (!app.isActive()) {
            throw new IllegalStateException("Application is not active.");
        }

        String plainClientSecret = generatePlainClientSecret();
        String hashedSecret = passwordEncoder.encode(plainClientSecret);
        app.setClientSecretHash(hashedSecret);
        applicationRepository.save(app);
        return RegenerateClientSecretResponse.builder().appId(appId).clientSecret(plainClientSecret).clientId(app.getClientId()).build();
    }

    @Transactional(readOnly = true)
    public RegenerateAuthorizeUrlResponse regenerateAuthorizeUrl(
        UUID developerId,
        UUID appId
    ) {
        DeveloperApplication app = getAndVerifyOwnership(appId, developerId);
        if (!app.isActive()) {
            throw new IllegalStateException("Application is not active.");
        }
        String authorizeUrl = oAuthLinkGenerator.generateLink(app);
        return RegenerateAuthorizeUrlResponse.builder().appId(appId).authorizeUrl(authorizeUrl).clientId(app.getClientId()).build();
    }

}
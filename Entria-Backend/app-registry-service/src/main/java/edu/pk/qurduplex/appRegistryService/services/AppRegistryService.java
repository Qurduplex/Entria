package edu.pk.qurduplex.appRegistryService.services;

import edu.pk.qurduplex.appRegistryService.dto.RegisterApplicationResponse;
import edu.pk.qurduplex.appRegistryService.models.DeveloperApplication;
import edu.pk.qurduplex.appRegistryService.repositories.DeveloperApplicationRepository;
import edu.pk.qurduplex.common.models.OAuthPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppRegistryService {

    private final DeveloperApplicationRepository applicationRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public RegisterApplicationResponse registerApplication(
            UUID developerId,
            String name,
            MultipartFile logo,
            MultipartFile tosPdf,
            Map<OAuthPermission, Boolean> permissions,
            String redirectUri){

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

        // todo: hash the client secret before storing it in the database
        String hashedSecret = plainClientSecret;

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

        return RegisterApplicationResponse.builder()
                .clientId(savedApplication.getClientId())
                .clientSecret(plainClientSecret)
                .name(savedApplication.getName())
                .redirectUri(savedApplication.getRedirectUri())
                .logoUrl(savedApplication.getLogoUrl())
                .build();
    }


}

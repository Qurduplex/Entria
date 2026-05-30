package edu.pk.qurduplex.appRegistryService.controllers;

import edu.pk.qurduplex.appRegistryService.dto.RegisterApplicationRequest;
import edu.pk.qurduplex.appRegistryService.dto.RegisterApplicationResponse;
import edu.pk.qurduplex.appRegistryService.services.AppRegistryService;
import edu.pk.qurduplex.common.models.OAuthPermission;
import edu.pk.qurduplex.common.models.UserRole;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "Application Registry", description = "Endpoints for developers to manage applications and validate project status")
public class AppRegistryController {


    private final AppRegistryService appRegistryService;

    @PostMapping("/register-application")
    public RegisterApplicationResponse registerApplication(
            @RequestHeader(value = "X-User-Id", required = false) UUID developerId,
            @RequestHeader(value = "X-User-Role", required = false) UserRole role,
            @Valid @ModelAttribute RegisterApplicationRequest request
    ) {

        log.info("Received application registration request");
        log.info("Developer Id from gateway: {}", developerId);
        log.info("Developer Role from Gateway: {}", role);
        log.info("Application name: {}", request.getName());
        log.info("Redirect URI: {}", request.getRedirectUri());
        log.info("Permissions: {}", request.getPermissions());
        if (request.getLogo() != null) log.info("Logo: {}", request.getLogo().getOriginalFilename());

        appRegistryService.registerApplication(
                developerId,
                request.getName(),
                request.getLogo(),
                request.getTosPdf(),
                request.getPermissions(),
                request.getRedirectUri()
        );

        return null;
    }
}

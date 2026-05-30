package edu.pk.qurduplex.appRegistryService.controllers;

import edu.pk.qurduplex.appRegistryService.dto.RegisterApplicationRequest;
import edu.pk.qurduplex.appRegistryService.dto.RegisterApplicationResponse;
import edu.pk.qurduplex.appRegistryService.exceptions.ForbiddenAccessException;
import edu.pk.qurduplex.appRegistryService.services.AppRegistryService;
import edu.pk.qurduplex.common.models.UserRole;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "Application Registry", description = "Endpoints for developers to manage applications and validate project status")
public class AppRegistryController {


    private final AppRegistryService appRegistryService;

    @PostMapping("/register-application")
    public ResponseEntity<RegisterApplicationResponse> registerApplication(
            @RequestHeader(value = "X-User-Id", required = true) UUID developerId,
            @RequestHeader(value = "X-User-Role", required = true) UserRole role,
            @Valid @ModelAttribute RegisterApplicationRequest request
    ) {

        if (role != UserRole.DEVELOPER) {
            throw new ForbiddenAccessException("Only developers can register new applications.");
        }

        RegisterApplicationResponse response = appRegistryService.registerApplication(
                developerId,
                request.getName(),
                request.getLogo(),
                request.getTosPdf(),
                request.getPermissions(),
                request.getRedirectUri()
        );

        return ResponseEntity.ok(response);
    }
}

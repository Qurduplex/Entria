package edu.pk.qurduplex.appRegistryService.controllers;

import edu.pk.qurduplex.appRegistryService.dto.ApplicationDetails;
import edu.pk.qurduplex.appRegistryService.dto.ApplicationSummaryResponse;
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

import java.util.List;
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
        verifyDeveloper(role);

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

    @GetMapping("/app-list")
    public ResponseEntity<List<ApplicationSummaryResponse>> getApplications(
            @RequestHeader(value = "X-User-Id") UUID developerId,
            @RequestHeader(value = "X-User-Role") UserRole role) {
        verifyDeveloper(role);

        return ResponseEntity.ok(appRegistryService.getDeveloperApplications(developerId));
    }

    @GetMapping("/details/{appId}")
    public ResponseEntity<ApplicationDetails> getApplicationDetails(
            @RequestHeader(value = "X-User-Id") UUID developerId,
            @RequestHeader(value = "X-User-Role") UserRole role,
            @PathVariable UUID appId) {

        verifyDeveloper(role);

        return ResponseEntity.ok(appRegistryService.getApplicationDetails(developerId, appId));
    }

    @PatchMapping("/deactivate/{appId}")
    public ResponseEntity<Void> deactivateApplication(
            @RequestHeader(value = "X-User-Id") UUID developerId,
            @RequestHeader(value = "X-User-Role") UserRole role,
            @PathVariable UUID appId) {

        verifyDeveloper(role);

        appRegistryService.deactivateApplication(appId, developerId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{appId}")
    public ResponseEntity<Void> deleteApplication(
            @RequestHeader(value = "X-User-Id") UUID developerId,
            @RequestHeader(value = "X-User-Role") UserRole role,
            @PathVariable UUID appId) {

        verifyDeveloper(role);

        appRegistryService.deleteApplication(appId, developerId);

        return ResponseEntity.noContent().build();
    }




    private void verifyDeveloper(UserRole role) {
        if (role != UserRole.DEVELOPER) {
            throw new ForbiddenAccessException("Only developers can perform this action.");
        }
    }
}

package edu.pk.qurduplex.appRegistryService.controllers;

import edu.pk.qurduplex.appRegistryService.dto.ApplicationDetails;
import edu.pk.qurduplex.appRegistryService.dto.ApplicationSummaryResponse;
import edu.pk.qurduplex.appRegistryService.dto.RegisterApplicationRequest;
import edu.pk.qurduplex.appRegistryService.dto.RegisterApplicationResponse;
import edu.pk.qurduplex.appRegistryService.dto.RegenerateClientSecretResponse;
import edu.pk.qurduplex.appRegistryService.dto.RegenerateAuthorizeUrlResponse;
import edu.pk.qurduplex.appRegistryService.dto.UpdateApplicationRequest;
import edu.pk.qurduplex.appRegistryService.dto.RegenerateClientSecretRequests;
import edu.pk.qurduplex.appRegistryService.dto.RegenerateAuthorizeUrlRequest;
import edu.pk.qurduplex.appRegistryService.exceptions.ForbiddenAccessException;
import edu.pk.qurduplex.appRegistryService.services.AppRegistryService;
import edu.pk.qurduplex.common.models.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Register a new OAuth2 application",
            description = "Registers a new application for a developer. Requires DEVELOPER role. Accepts multipart/form-data including application logo and Terms of Service PDF."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Application successfully registered",
                    content = @Content(schema = @Schema(implementation = RegisterApplicationResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request data or validation failed",
                    content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden - User is not a developer",
                    content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "409",
                    description = "Conflict - Application name is already taken",
                    content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error (e.g., file storage failure)",
                    content = @Content(schema = @Schema(implementation = java.util.Map.class)))
    })
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

    @Operation(
            summary = "Get developer's applications",
            description = "Retrieves a summary list of all OAuth2 applications registered by the authenticated developer."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of applications retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApplicationSummaryResponse.class)))),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden - User is not a developer",
                    content = @Content(schema = @Schema(implementation = java.util.Map.class)))
    })
    @GetMapping("/app-list")
    public ResponseEntity<List<ApplicationSummaryResponse>> getApplications(
            @RequestHeader(value = "X-User-Id") UUID developerId,
            @RequestHeader(value = "X-User-Role") UserRole role) {
        verifyDeveloper(role);

        return ResponseEntity.ok(appRegistryService.getDeveloperApplications(developerId));
    }

    @Operation(
            summary = "Get application details",
            description = "Retrieves detailed information (including credentials and statistics) about a specific application. The application must belong to the requesting developer."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Application details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApplicationDetails.class))),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden - User is not a developer or does not own this application",
                    content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "404",
                    description = "Application not found",
                    content = @Content(schema = @Schema(implementation = java.util.Map.class)))
    })
    @GetMapping("/details/{appId}")
    public ResponseEntity<ApplicationDetails> getApplicationDetails(
            @RequestHeader(value = "X-User-Id") UUID developerId,
            @RequestHeader(value = "X-User-Role") UserRole role,
            @PathVariable UUID appId) {

        verifyDeveloper(role);

        return ResponseEntity.ok(appRegistryService.getApplicationDetails(developerId, appId));
    }

    @Operation(
            summary = "Deactivate application",
            description = "Deactivates a specific application, preventing further OAuth2 authorizations. The application must belong to the requesting developer."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Application deactivated successfully (No content)"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden - User is not a developer or does not own this application",
                    content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "404",
                    description = "Application not found",
                    content = @Content(schema = @Schema(implementation = java.util.Map.class)))
    })
    @PatchMapping("/deactivate/{appId}")
    public ResponseEntity<Void> deactivateApplication(
            @RequestHeader(value = "X-User-Id") UUID developerId,
            @RequestHeader(value = "X-User-Role") UserRole role,
            @PathVariable UUID appId) {

        verifyDeveloper(role);

        appRegistryService.deactivateApplication(appId, developerId);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Delete application",
            description = "Permanently deletes a specific application and all associated files (logo, TOS). The application must belong to the requesting developer."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Application deleted successfully (No content)"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden - User is not a developer or does not own this application",
                    content = @Content(schema = @Schema(implementation = java.util.Map.class))),
            @ApiResponse(responseCode = "404",
                    description = "Application not found",
                    content = @Content(schema = @Schema(implementation = java.util.Map.class)))
    })
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

    @Operation(
        summary = "Update application",
        description = "Updates a specific application. The application must belong to the requesting developer."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "Application updated successfully",
            content = @Content(schema = @Schema(implementation = ApplicationDetails.class))),
        @ApiResponse(responseCode = "400",
            description = "Invalid request data or validation failed",
            content = @Content(schema = @Schema(implementation = java.util.Map.class))),
        @ApiResponse(responseCode = "403",
            description = "Forbidden - User is not a developer or does not own this application",
            content = @Content(schema = @Schema(implementation = java.util.Map.class))),
        @ApiResponse(responseCode = "404",
            description = "Application not found",
            content = @Content(schema = @Schema(implementation = java.util.Map.class))),
        @ApiResponse(responseCode = "409",
            description = "Conflict - Application name is already taken",
            content = @Content(schema = @Schema(implementation = java.util.Map.class)))
    })
    @PatchMapping("/update-application")
    public ResponseEntity<ApplicationDetails> updateApplication(
        @RequestHeader(value = "X-User-Id") UUID developerId,
        @RequestHeader(value = "X-User-Role") UserRole role,
        @Valid @ModelAttribute UpdateApplicationRequest request
    ) {
        verifyDeveloper(role);

        ApplicationDetails response = appRegistryService.updateApplication(
                developerId, 
                request.getAppId(), 
                request.getName(), 
                request.getRedirectUri(), 
                request.getLogo(), 
                request.getTosPdf(), 
                request.getPermissions());
                

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Regenerate client secret",
        description = "Regenerates the client secret for a specific application. The application must belong to the requesting developer."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "Client secret regenerated successfully",
            content = @Content(schema = @Schema(implementation = RegenerateClientSecretResponse.class))),
        @ApiResponse(responseCode = "403",
            description = "Forbidden - User is not a developer or does not own this application",
            content = @Content(schema = @Schema(implementation = java.util.Map.class))),
        @ApiResponse(responseCode = "404",
            description = "Application not found",
            content = @Content(schema = @Schema(implementation = java.util.Map.class)))
    })
    @PostMapping("/regenerate-client-secret")
    public ResponseEntity<RegenerateClientSecretResponse> regenerateClientSecret(
        @RequestHeader(value = "X-User-Id") UUID developerId,
        @RequestHeader(value = "X-User-Role") UserRole role,
        @Valid @RequestBody RegenerateClientSecretRequests request
    ) {
        verifyDeveloper(role);

        RegenerateClientSecretResponse response = appRegistryService.regenerateClientSecret(developerId, request.getAppId());

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Regenerate authorize URL",
        description = "Regenerates the authorize URL for a specific application. The application must belong to the requesting developer."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "Authorize URL regenerated successfully",
            content = @Content(schema = @Schema(implementation = RegenerateAuthorizeUrlResponse.class))),
        @ApiResponse(responseCode = "400",
            description = "Invalid request data or validation failed",
            content = @Content(schema = @Schema(implementation = java.util.Map.class))),
        @ApiResponse(responseCode = "403",
            description = "Forbidden - User is not a developer or does not own this application",
            content = @Content(schema = @Schema(implementation = java.util.Map.class))),
        @ApiResponse(responseCode = "404",
            description = "Application not found",
            content = @Content(schema = @Schema(implementation = java.util.Map.class)))
    })
    @PostMapping("/regenerate-authorize-url")
    public ResponseEntity<RegenerateAuthorizeUrlResponse> regenerateAuthorizeUrl(
        @RequestHeader(value = "X-User-Id") UUID developerId,
        @RequestHeader(value = "X-User-Role") UserRole role,
        @Valid @RequestBody RegenerateAuthorizeUrlRequest request
    ) {
        verifyDeveloper(role);
        return ResponseEntity.ok(
                appRegistryService.regenerateAuthorizeUrl(developerId, request.getAppId())
        );
    }

}

package edu.pk.qurduplex.userDataService.controllers;

import edu.pk.qurduplex.userDataService.exceptions.JwtAuthenticationException;
import edu.pk.qurduplex.userDataService.services.UserProfileService;
import edu.pk.qurduplex.userDataService.dto.*;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.pk.qurduplex.userDataService.mappers.UserProfileMapper;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user-profile")
@Tag(name = "User Profile", description = "Endpoints for user profile management")
public class UserProfileController {
    private final UserProfileService userProfileService;


    @Operation(
        summary = "Get user profile",
        description = "Get user profile by user id"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile found", content = @Content(schema = @Schema(implementation = UserProfileResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Invalid authorization header", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
        @ApiResponse(responseCode = "404", description = "User profile not found", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = java.util.Map.class)))
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(
            @RequestHeader(value = "X-User-Id", required = true) String userId
    ) {
        if(userId == null || userId.isEmpty()) {
            throw new JwtAuthenticationException("Missing User ID header");
        }
        return ResponseEntity.ok(UserProfileMapper.toResponseDTO(userProfileService.getUserProfile(UUID.fromString(userId))));
    }

    @Operation(
        summary = "Update user profile",
        description = "Update user profile by user id"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile updated", content = @Content(schema = @Schema(implementation = UserProfileResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Invalid authorization header", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
        @ApiResponse(responseCode = "404", description = "User profile not found", content = @Content(schema = @Schema(implementation = java.util.Map.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = java.util.Map.class)))
    })
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> updateUserProfile(
            @RequestHeader(value = "X-User-Id", required = true) String userId,
            @ModelAttribute @Valid UpdateUserProfileRequestDTO request
    ) {
        if(userId == null || userId.isEmpty()) {
            throw new JwtAuthenticationException("Missing User ID header");
        }

        return ResponseEntity.ok(UserProfileMapper.toResponseDTO(
                userProfileService.updateUserProfile(
                        UUID.fromString(userId),
                        UserProfileMapper.toUserProfileUpdates(request),
                        request.getProfilePicture()
        )));
    }
}

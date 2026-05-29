package edu.pk.qurduplex.userDataService.controllers;

import edu.pk.qurduplex.userDataService.services.UserProfileService;
import edu.pk.qurduplex.userDataService.dto.*;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import edu.pk.qurduplex.userDataService.exceptions.JwtAuthenticationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.pk.qurduplex.userDataService.models.UserProfile;
import edu.pk.qurduplex.userDataService.services.JwtService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user-profile")
@Tag(name = "User Profile", description = "Endpoints for user profile management")
public class UserProfileController {
    private final UserProfileService userProfileService;
    private final JwtService jwtService;

    private UserProfileResponseDTO toResponseDto(UserProfile userProfile) {
        return UserProfileResponseDTO.builder()
                .userId(userProfile.getUserId())
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .email(userProfile.getEmail())
                .phoneNumber(userProfile.getPhoneNumber())
                .pesel(userProfile.getPesel())
                .sex(userProfile.getSex())
                .birthDate(userProfile.getBirthDate() != null ? userProfile.getBirthDate().toString() : null)
                .profilePictureUrl(userProfile.getProfilePictureUrl())
                .build();
    }
    
    //Zdobywanie informacji o profilu uzytkownika
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
        @RequestHeader("Authorization") String authHeader
    ) {
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JwtAuthenticationException("Invalid authorization header");
        }
        String token = authHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return ResponseEntity.ok(toResponseDto(userProfileService.getUserProfile(UUID.fromString(userId))));
    }


    //Aktualizowanie informacji o profilu uzytkownika
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
        @RequestHeader("Authorization") String authHeader,
        @RequestBody @Valid UpdateUserProfileRequestDTO request
    ) {
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JwtAuthenticationException("Invalid authorization header");
        }
        String token = authHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return ResponseEntity.ok( toResponseDto(userProfileService.updateUserProfile(UUID.fromString(userId), request)));
    }
}

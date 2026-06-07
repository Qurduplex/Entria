package edu.pk.qurduplex.oauthService.controllers;

import edu.pk.qurduplex.oauthService.dto.UserAuthorizedAppDTO;
import edu.pk.qurduplex.oauthService.services.UserConsentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/user/consents")
@RequiredArgsConstructor
public class UserAuthorizedAppsController {

    private final UserConsentService userConsentService;

    @GetMapping
    public ResponseEntity<List<UserAuthorizedAppDTO>> getUserConsents(
            @RequestHeader(value = "X-User-Id", required = true) String userId
    ) {
        if (userId == null || userId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing User ID header");
        }

        List<UserAuthorizedAppDTO> response = userConsentService.getUserConsents(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> revokeConsent(
            @RequestHeader(value = "X-User-Id", required = true) String userId,
            @PathVariable String clientId
    ) {
        if (userId == null || userId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing User ID header");
        }

        userConsentService.revokeConsent(userId, clientId);
        return ResponseEntity.noContent().build();
    }
}
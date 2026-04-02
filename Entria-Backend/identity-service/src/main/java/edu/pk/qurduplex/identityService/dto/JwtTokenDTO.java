package edu.pk.qurduplex.identityService.dto;

import java.time.Instant;

public record JwtTokenDTO(String token, Instant expiresAt) {}

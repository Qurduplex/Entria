package edu.pk.qurduplex.identityService.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("identity-service:reset-password-codes")
public class ResetPasswordCode {
    @Id
    private UUID id;

    private String code;

    @TimeToLive
    private Long expirationInSeconds;
}
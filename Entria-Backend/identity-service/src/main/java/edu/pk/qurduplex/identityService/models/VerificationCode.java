package edu.pk.qurduplex.identityService.models;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("identity-service:verification-codes")
public class VerificationCode {

    @Id
    private UUID id;

    private String code;

    @TimeToLive
    private Long expirationInSeconds;
}
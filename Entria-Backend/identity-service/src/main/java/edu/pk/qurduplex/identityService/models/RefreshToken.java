package edu.pk.qurduplex.identityService.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("identity-service:refresh-tokens")
public class RefreshToken {

    @Id
    private UUID token;

    @Indexed
    private UUID userId;

    @TimeToLive
    private Long expirationInSeconds;
}
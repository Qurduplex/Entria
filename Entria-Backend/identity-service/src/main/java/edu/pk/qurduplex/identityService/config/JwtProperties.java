package edu.pk.qurduplex.identityService.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtProperties {
    private String secretKey;
    private Duration expiration;
    private Duration refreshExpiration;
}

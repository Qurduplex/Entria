package edu.pk.qurduplex.identityService.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "application.verification-code")
public class VerificationCodeProperties {
    private Long expirationInSeconds;
    private Integer codeLength;
}

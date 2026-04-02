package edu.pk.qurduplex.identityService.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "application.reset-password-code")
public class ResetPasswordCodeProperties {
    private Long expirationInSeconds;
    private Integer codeLength;
}

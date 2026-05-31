package edu.pk.qurduplex.appRegistryService.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "application.oauth2")
public class OauthProperties {
    private String authorizeUrl;
}
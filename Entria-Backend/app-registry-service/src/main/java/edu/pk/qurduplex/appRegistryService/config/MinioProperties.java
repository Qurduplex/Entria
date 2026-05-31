package edu.pk.qurduplex.appRegistryService.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "application.minio")
public class MinioProperties {
    private String url;
    private String externalUrl;
    private String accessKey;
    private String secretKey;
    private String publicBucket;
}
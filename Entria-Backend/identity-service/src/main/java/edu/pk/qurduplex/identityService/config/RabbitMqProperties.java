package edu.pk.qurduplex.identityService.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "application.rabbitmq")
public class RabbitMqProperties {
    private String exchangeName;

    private String verificationCodeRoutingKey;
    private String resetPasswordCodeRoutingKey;
    private String userRegisteredRoutingKey;
}
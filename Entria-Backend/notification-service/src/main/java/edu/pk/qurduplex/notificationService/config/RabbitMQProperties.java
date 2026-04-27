package edu.pk.qurduplex.notificationService.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "application.rabbitmq")
public class RabbitMQProperties {
    private String exchangeName;

    private String resetPasswordCodeEventTopic;
    private String verificationCodeEventTopic;

    private String resetPasswordCodeEventRoutingKey;
    private String verificationCodeEventRoutingKey;
}

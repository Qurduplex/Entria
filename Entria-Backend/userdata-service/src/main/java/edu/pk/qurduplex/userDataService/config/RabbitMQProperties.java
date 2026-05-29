package edu.pk.qurduplex.userDataService.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "application.rabbitmq")
public class RabbitMQProperties {
    private String exchangeName;
    private String userRegisteredEventTopic;
    private String userRegisteredEventRoutingKey;
}

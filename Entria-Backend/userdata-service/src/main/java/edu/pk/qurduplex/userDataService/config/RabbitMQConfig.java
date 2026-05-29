package edu.pk.qurduplex.userDataService.config;

import org.springframework.context.annotation.Bean;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;

import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {
    private final RabbitMQProperties rabbitMQProperties;

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(rabbitMQProperties.getExchangeName());
    }

    @Bean
    Queue userRegisteredEventQueue() {
        return new Queue(rabbitMQProperties.getUserRegisteredEventTopic());
    }

    @Bean
    Binding userRegisteredEventBinding(Queue userRegisteredEventQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userRegisteredEventQueue).to(exchange).with(rabbitMQProperties.getUserRegisteredEventRoutingKey());
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

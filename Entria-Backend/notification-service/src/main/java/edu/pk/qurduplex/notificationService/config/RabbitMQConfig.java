package edu.pk.qurduplex.notificationService.config;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class RabbitMQConfig {

    private final RabbitMQProperties rabbitMqProperties;

    @Bean
    public Queue resetPasswordCodeEventQueue() {
        return new Queue(rabbitMqProperties.getResetPasswordCodeEventTopic());
    }

    @Bean
    public Queue verificationCodeEventQueue() {
        return new Queue(rabbitMqProperties.getVerificationCodeEventTopic());
    }


    @Bean
    public TopicExchange  exchange() {
        return new TopicExchange (rabbitMqProperties.getExchangeName());
    }


    @Bean
    public Binding resetPasswordCodeEventBinding(
            @Qualifier("resetPasswordCodeEventQueue") Queue queue,
            TopicExchange  exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(rabbitMqProperties.getResetPasswordCodeEventRoutingKey());
    }

    @Bean
    public Binding verificationCodeEventBinding(
            @Qualifier("verificationCodeEventQueue") Queue queue,
            TopicExchange  exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(rabbitMqProperties.getVerificationCodeEventRoutingKey());
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

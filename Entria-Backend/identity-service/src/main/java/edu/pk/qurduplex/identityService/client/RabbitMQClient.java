package edu.pk.qurduplex.identityService.client;

import edu.pk.qurduplex.common.messages.user.CreateProfileMessage;
import edu.pk.qurduplex.common.messages.user.ResetPasswordCodeMessage;
import edu.pk.qurduplex.common.messages.user.VerificationCodeMessage;
import edu.pk.qurduplex.identityService.config.RabbitMqProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RabbitMQClient {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties rabbitMqProperties;

    public void sendVerificationCodeMessage(String email, String verificationCode) {
        VerificationCodeMessage event = new VerificationCodeMessage(email, verificationCode);

        rabbitTemplate.convertAndSend(
                rabbitMqProperties.getExchangeName(),
                rabbitMqProperties.getVerificationCodeRoutingKey(),
                event
        );
    }

    public void sendResetPasswordCodeMessage(String email, String resetPasswordCode) {
        ResetPasswordCodeMessage event = new ResetPasswordCodeMessage(email, resetPasswordCode);

        rabbitTemplate.convertAndSend(
                rabbitMqProperties.getExchangeName(),
                rabbitMqProperties.getResetPasswordCodeRoutingKey(),
                event
        );
    }

    public void sendCreateProfileMessage(UUID userId, String firstName, String lastName) {
        CreateProfileMessage event = new CreateProfileMessage(userId, firstName, lastName);

        rabbitTemplate.convertAndSend(
                rabbitMqProperties.getExchangeName(),
                rabbitMqProperties.getUserRegisteredRoutingKey(),
                event
        );
    }


}
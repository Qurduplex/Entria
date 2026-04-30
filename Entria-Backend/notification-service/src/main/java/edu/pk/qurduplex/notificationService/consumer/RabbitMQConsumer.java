package edu.pk.qurduplex.notificationService.consumer;

import edu.pk.qurduplex.common.messages.user.ResetPasswordCodeMessage;
import edu.pk.qurduplex.common.messages.user.VerificationCodeMessage;
import edu.pk.qurduplex.notificationService.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "#{rabbitMQProperties.getVerificationCodeEventTopic()}")
    public void processVerificationCodeMessage(VerificationCodeMessage message) {
        log.info("Received verification code message for email: {}", message.getEmail());

        emailService.sendVerificationEmail(message.getEmail(), message.getCode());
    }

    @RabbitListener(queues = "#{rabbitMQProperties.getResetPasswordCodeEventTopic()}")
    public void processResetPasswordCodeMessage(ResetPasswordCodeMessage message) {
        log.info("Received reset password code message for email: {}", message.getEmail());

        emailService.sendResetPasswordEmail(message.getEmail(), message.getCode());
    }
}
